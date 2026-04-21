(ns api.routes.authentication
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
        {:email email :password password}))))

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
        (ring.util.response/set-cookie "token" token {:http-only true
                                                      :secure secure
                                                      :same-site :lax
                                                      :path "/"
                                                      :max-age (* max-age 3600)}))))

(defn login-error-handler
  [now secure]
  (let [body {:messaging/messages {::login-error-handler
                                   {:message/status :error
                                    :message/text "Login Failed"
                                    :message/timestamp (.toString now)}}}]
    (-> (ring.util.response/response body)
        (ring.util.response/status 401)
        (ring.util.response/set-cookie "token" "" {:http-only true :path "/" :max-age -1 :secure secure}))))

(defn get-routes
  [authentication]
  ["/authentication"

   ["/login"
    {:name        ::login
     :middleware  [[buddy.auth.middleware/wrap-authentication authentication]]
     :get         {:summary   "authenticates a user"
                   :handler   (fn [{{{query-worker :query} :workers} :database configuration :configuration :as request}]
                                (let [now    (java.time.Instant/now)
                                      secure (get-in configuration [:api :authentication :secure])
                                      {email :email password :password} (decode-basic-authentication request)]
                                  (if-let [[[private-user]] (when (and email password) (common.entities.user/get-user-by-email query-worker email))]
                                    (if-let [public-user (and private-user (buddy.hashers/check password (:user/hash private-user)) (common.entities.user/private->public private-user))]
                                      (login-success-handler now configuration public-user)
                                      (login-error-handler now secure))
                                    (login-error-handler now secure))))}}]

   ["/logout"
    {:name        ::logout
     :get         {:summary   "unauthenticates a user"
                   :handler   (fn [{configuration :configuration}]
                                (let [now    (java.time.Instant/now)
                                      secure (get-in configuration [:api :authentication :secure])
                                      body   {:messaging/messages {::get-routes
                                                                   {:message/status :success
                                                                    :message/text "Logout Successful"
                                                                    :message/timestamp (.toString now)}}}]
                                  (-> (ring.util.response/response body)
                                      (ring.util.response/status 200)
                                      (ring.util.response/set-cookie "token" "" {:http-only true :path "/" :max-age -1 :secure secure}))))}}]])
