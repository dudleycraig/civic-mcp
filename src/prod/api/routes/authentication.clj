(ns api.routes.authentication
  "semantic adherence to RFC 9110, RFC 7617, RFC 6265bis, RFC 7519 and OWASP Security"
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

(defn decode-basic-authentication
  [request]
  (when-let [authentication-header (get-in request [:headers "authorization"])]
    (when (clojure.string/starts-with? authentication-header "Basic ")
      (let [decoded (String. (.decode (java.util.Base64/getDecoder) (subs authentication-header 6)) "UTF-8")
            [email password] (clojure.string/split decoded #":" 2)]
        {:user/email email :user/password password}))))

(defn login-success-handler
  [now configuration public-user]
  (let [auth-config (:authentication (:api configuration))
        max-age     (:max-age auth-config)
        secure      (:secure auth-config)
        claim-config (:claim auth-config)
        exp         (.plus now max-age java.time.temporal.ChronoUnit/HOURS)
        claim       {:iat (.getEpochSecond now)
                     :exp (.getEpochSecond exp)
                     :iss (:iss claim-config)
                     :aud (:aud claim-config)
                     :sub (:user/uuid public-user)
                     :jti (common.entities.utilities/generate-uuid)
                     :data {:user/email (:user/email public-user)
                            :user/roles (:user/roles public-user)}}
        token       (api.system.authentication/encode-token configuration claim)
        body        {:user public-user
                     :messaging/messages {::login-success-handler
                                          {:message/status :success
                                           :message/text "Login Successful"
                                           :message/timestamp (.toString now)}}}]
    (-> (ring.util.response/response body)
        (ring.util.response/status 200)
        (ring.util.response/header "Cache-Control" "no-store")
        (ring.util.response/set-cookie "token" token {:http-only true :secure secure :same-site :lax :path "/" :max-age (* max-age 3600)}))))

(defn login-error-handler
  ([now secure]
   (login-error-handler now secure "Invalid email or password"))
  ([now secure message]
   (let [body {:messaging/messages {::login-error-handler
                                    {:message/status :error
                                     :message/text message
                                     :message/timestamp (.toString now)}}}]
     (-> (ring.util.response/response body)
         (ring.util.response/status 401)
         ;; we deviate slightly from RFC 9110 as the browser popup is more a hinderance.
         ;; (ring.util.response/header "WWW-Authenticate" "Basic realm=\"CIVIC ZA API\"")
         (ring.util.response/header "Cache-Control" "no-store")
         (ring.util.response/set-cookie "token" "" {:http-only true :secure secure :path "/" :max-age -1})))))

(defn get-routes
  [authentication]
  ["/authentication"
   ["/login"
    {:name        ::login
     :middleware  [[buddy.auth.middleware/wrap-authentication authentication]]
     :post        {:summary   "Basic user authentication."
                   :handler   (fn [{{{query-worker :query} :workers} :database configuration :configuration :as request}]
                                (let [now    (java.time.Instant/now)
                                      secure (get-in configuration [:api :authentication :secure])
                                      {email :user/email password :user/password} (decode-basic-authentication request)]
                                  (if-let [[[private-user]] (when (and email password) (common.entities.user/get-user-by-email query-worker email))]
                                    (if-let [public-user (and private-user (buddy.hashers/check password (:user/hash private-user)) (common.entities.user/private->public private-user))]
                                      (login-success-handler now configuration public-user)
                                      (login-error-handler now secure))
                                    (login-error-handler now secure))))}}]

   ["/logout"
    {:name        ::logout
     :post         {:summary   "unauthenticates a user"
                    :handler   (fn [{configuration :configuration}]
                                 (let [now    (java.time.Instant/now)
                                       secure (get-in configuration [:api :authentication :secure])
                                       body   {:messaging/messages {::get-routes
                                                                    {:message/status :success
                                                                     :message/text "Logout Successful"
                                                                     :message/timestamp (.toString now)}}}]
                                   (-> (ring.util.response/response body)
                                       (ring.util.response/status 200)
                                       (ring.util.response/header "Cache-Control" "no-store")
                                       (ring.util.response/set-cookie "token" "" {:http-only true :path "/" :max-age -1 :secure secure}))))}}]])





