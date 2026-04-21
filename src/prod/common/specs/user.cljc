(ns common.specs.user
  (:require [clojure.spec.alpha]
            [common.regex]
            [common.specs.utilities]))

(clojure.spec.alpha/def :user/email (clojure.spec.alpha/and string? #(re-matches common.regex/email %)))
(clojure.spec.alpha/def :user/password (clojure.spec.alpha/and string? #(re-matches common.regex/password %)))
(clojure.spec.alpha/def :user/hash string?)
(clojure.spec.alpha/def :user/uuid uuid?)
(clojure.spec.alpha/def :user/first-name string?)
(clojure.spec.alpha/def :user/last-name string?)
(clojure.spec.alpha/def :user/roles (clojure.spec.alpha/coll-of :role/spec))

;; a generic user, likely no use case for it currently
(clojure.spec.alpha/def :user/spec
  (clojure.spec.alpha/and
   (clojure.spec.alpha/keys
    :req [:user/email
          :user/first-name
          :user/last-name]
    :opt [:user/uuid :user/password :user/hash :user/roles])))

;; a user that contains a plain text :user/password
(clojure.spec.alpha/def :user/transient
  (clojure.spec.alpha/and
   (clojure.spec.alpha/keys
    :req [:user/email
          :user/first-name
          :user/last-name
          :user/password]
    :opt [:user/roles])
   (common.specs.utilities/not-contains-many? :user/hash :user/uuid)))

;; a user that contains a hashed password :user/hash
(clojure.spec.alpha/def :user/private
  (clojure.spec.alpha/and
   (clojure.spec.alpha/keys
    :req [:user/email
          :user/first-name
          :user/last-name
          :user/roles
          :user/hash
          :user/uuid]
    :opt [])
   (common.specs.utilities/not-contains-many? :user/password)))

;; a user that doesn't contain a hashed password :user/hash
(clojure.spec.alpha/def :user/public
  (clojure.spec.alpha/and
   (clojure.spec.alpha/keys
    :req [:user/email
          :user/first-name
          :user/last-name
          :user/roles
          :user/uuid]
    :opt [])
   (common.specs.utilities/not-contains-many? :user/password :user/hash)))




