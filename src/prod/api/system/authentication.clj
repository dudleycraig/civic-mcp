(ns api.system.authentication
  (:require
   [integrant.core]
   [clojure.spec.alpha]
   [clojure.tools.logging]
   [clojure.string]
   [ring.util.response]
   [buddy.auth]
   [buddy.hashers]
   [buddy.core.keys]
   [buddy.auth.protocols]
   [buddy.sign.jwt]
   [common.specs.authentication]))

(defn encode-token
  ([{{{algorithm :alg private-key :private-key} :authentication} :api} claim]
   (buddy.sign.jwt/sign claim (buddy.core.keys/private-key private-key) {:alg algorithm})))

(defn default-unauthorized-handler
  [secure request _metadata]
  (if (buddy.auth/authenticated? request)
    (-> (ring.util.response/response nil)
        (ring.util.response/status 403)
        (ring.util.response/set-cookie "session-token" "" {:http-only true :path "/" :max-age -1 :secure secure}))
    (-> (ring.util.response/response nil)
        (ring.util.response/status 401)
        (ring.util.response/set-cookie "session-token" "" {:http-only true :path "/" :max-age -1 :secure secure}))))

(defn authenticate-jwt
  [{{{{configured-issuer :iss configured-audience :aud} :claim} :authentication} :api} _database token-claim]
  (if (clojure.spec.alpha/valid? :auth/jwt-claim token-claim)
    (let [{token-issuer   :iss
           token-audience :aud
           user-uuid      :sub
           user-jti       :jti
           user-data      :data} token-claim]
      (if (and (= token-issuer configured-issuer)
               (= token-audience configured-audience)
               (not (clojure.string/blank? (str user-jti))))
        (assoc user-data :user/uuid user-uuid :auth/jti user-jti)
        (throw
         (ex-info
          "Authentication Error, Invalid JWT Token Issuer or Audience"
          {::authenticate-jwt
           {:message/status :error
            :message/code 400}}))))
    (throw
     (ex-info
      "Authentication Error, Invalid JWT Token Structure"
      {::authenticate-jwt
       {:message/status :error
        :message/code 400
        :message/explanation (clojure.spec.alpha/explain-data :auth/jwt-claim token-claim)}}))))

(defn cookie-auth-backend
  [{{{algorithm :alg public-key :public-key secure :secure} :authentication} :api :as configuration} database]
  (let [authfn                (partial authenticate-jwt configuration database)
        unauthorized-handler  (partial default-unauthorized-handler secure)
        public-key            (buddy.core.keys/public-key public-key)
        options               {:alg algorithm}
        cookie-name           "session-token"
        on-error              (fn [_request _token exception]
                                (clojure.tools.logging/warn "JWT Verification Failed:" (ex-message exception))
                                nil)]
    (reify
      buddy.auth.protocols/IAuthentication
      (-parse [_ request]
        (get-in request [:cookies cookie-name :value]))
      (-authenticate [_ request token]
        (try
          (if (clojure.string/blank? token)
            nil
            (authfn (buddy.sign.jwt/unsign token public-key options)))
          (catch Exception exception
            (on-error request token exception))))

      buddy.auth.protocols/IAuthorization
      (-handle-unauthorized [_ request metadata]
        (if (fn? unauthorized-handler)
          (unauthorized-handler request metadata)
          (default-unauthorized-handler secure request metadata))))))

(defmethod integrant.core/init-key ::service
  [_ {configuration :configuration database :database}]
  (cookie-auth-backend configuration database))

(defmethod integrant.core/halt-key! ::service
  [_ _])
