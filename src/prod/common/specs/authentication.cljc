(ns common.specs.authentication
  (:require [clojure.spec.alpha]
            [common.specs.user]
            [common.regex]))

(clojure.spec.alpha/def :auth/iss string?)
(clojure.spec.alpha/def :auth/aud string?)
(clojure.spec.alpha/def :auth/sub (clojure.spec.alpha/or :uuid uuid? :string string?))
(clojure.spec.alpha/def :auth/iat number?)
(clojure.spec.alpha/def :auth/exp number?)
(clojure.spec.alpha/def :auth/jti (clojure.spec.alpha/or :uuid uuid? :string string?))
(clojure.spec.alpha/def :auth/data (clojure.spec.alpha/keys :req [:user/email :user/roles]))

(clojure.spec.alpha/def :auth/jwt-claim
  (clojure.spec.alpha/keys
   :req-un [:auth/iss
            :auth/aud
            :auth/sub
            :auth/iat
            :auth/exp
            :auth/jti
            :auth/data]))


(clojure.spec.alpha/def :auth/configuration
  (clojure.spec.alpha/keys
   :req-un [:api/authentication]))
