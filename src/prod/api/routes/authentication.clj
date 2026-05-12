(ns api.routes.authentication
  "semantic adherence to RFC 9110, RFC 7617, RFC 6265bis, RFC 7519, RFC 6750, RFC 8725 and OWASP Security"
  (:require
   [clojure.spec.alpha]
   [clojure.string]
   [ring.util.response]
   [reitit.ring]
   [buddy.auth]
   [buddy.auth.middleware]
   [buddy.hashers]
   [common.entities.user]
   [common.entities.utilities]
   [api.system.authentication]))

(defn csrf-middleware
  []
  {:name ::csrf
   :wrap (fn [handler]
           (fn [request]
             (let [token-cookie (get-in request [:cookies "csrf-token" :value])
                   token-header (get-in request [:headers "x-csrf-token"])
                   http-method  (:request-method request)]
               (if (contains? #{:post :put :delete :patch} http-method)
                 (if (and token-cookie token-header (= token-cookie token-header))
                   (handler request)
                   (->
                    (ring.util.response/response nil)
                    (ring.util.response/status 403)))
                 (let [response (handler request)]
                   (if (clojure.string/blank? token-cookie)
                     (let [new-token (clojure.string/replace (.toString (java.util.UUID/randomUUID)) #"-" "")]
                       (ring.util.response/set-cookie response "csrf-token" new-token {:http-only false :path "/" :same-site :lax}))
                     response))))))})

(defn decode-basic-authentication
  [request]
  (when-let [authentication-header (get-in request [:headers "authorization"])]
    (when (clojure.string/starts-with? authentication-header "Basic ")
      (let [decoded (String. (.decode (java.util.Base64/getDecoder) (subs authentication-header 6)) "UTF-8")
            [email password] (clojure.string/split decoded #":" 2)]
        {:user/email email :user/password password}))))

(defn login-success-handler
  [configuration public-user]
  (let [now           (java.time.Instant/now)
        auth-config   (:authentication (:api configuration))
        max-age       (:max-age auth-config)
        secure        (:secure auth-config)
        claim-config  (:claim auth-config)
        exp           (.plus now max-age java.time.temporal.ChronoUnit/HOURS)
        jti           (common.entities.utilities/generate-uuid)
        claim         {:iat (.getEpochSecond now)
                       :exp (.getEpochSecond exp)
                       :iss (:iss claim-config)
                       :aud (:aud claim-config)
                       :sub (:user/uuid public-user)
                       :jti jti
                       :data {:user/email (:user/email public-user)
                              :user/roles (:user/roles public-user)}}
        token         (api.system.authentication/encode-token configuration claim)]
    (-> (ring.util.response/response (assoc public-user :auth/jti jti))
        (ring.util.response/status 200)
        (ring.util.response/header "Cache-Control" "no-store")
        (ring.util.response/set-cookie "session-token" token {:http-only true :secure secure :same-site :lax :path "/" :max-age (* max-age 3600)}))))

(defn login-error-handler
  [configuration]
  (let [secure (get-in configuration [:api :authentication :secure])]
    (-> (ring.util.response/response nil)
        (ring.util.response/status 401)
        (ring.util.response/header "Cache-Control" "no-store")
        (ring.util.response/set-cookie "session-token" "" {:http-only true :secure secure :path "/" :max-age -1}))))

(defn get-routes
  [authentication]
  ["/authentication"
   ["/login"
    {:name        ::login
     :middleware  [[buddy.auth.middleware/wrap-authentication authentication]
                   (csrf-middleware)]
     :post        {:summary   "instantiates a session"
                   :handler   (fn [{{{query-worker :query} :workers} :database configuration :configuration :as request}]
                                (let [{email :user/email password :user/password} (decode-basic-authentication request)]
                                  (if-let [[[private-user]] (when (and email password) (common.entities.user/get-user-by-email query-worker email))]
                                    (if-let [public-user (and private-user (buddy.hashers/check password (:user/hash private-user)) (common.entities.user/private->public private-user))]
                                      (login-success-handler configuration public-user)
                                      (login-error-handler configuration))
                                    (login-error-handler configuration))))}}]

   ["/session"
    ["/verify"
     {:name        ::session
      :middleware  [[buddy.auth.middleware/wrap-authentication authentication]]
      :get         {:summary   "verifies an existing session"
                    :handler   (fn [{configuration :configuration :as request}]
                                 (if-let [identity (:identity request)]
                                   (ring.util.response/response identity)
                                   (login-error-handler configuration)))}}]]

   ["/logout"
    {:name        ::logout
     :post        {:summary   "deletes a session"
                   :handler   (fn [{configuration :configuration}]
                                (let [secure (get-in configuration [:api :authentication :secure])]
                                  (-> (ring.util.response/response nil)
                                      (ring.util.response/status 200)
                                      (ring.util.response/header "Cache-Control" "no-store")
                                      (ring.util.response/set-cookie "session-token" "" {:http-only true :path "/" :max-age -1 :secure secure}))))}}]])





