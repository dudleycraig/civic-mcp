(ns common.specs.user-role
  (:require [clojure.spec.alpha :as s]
            [common.regex]))

(s/def :user-role/user-id int?)
(s/def :user-role/role-id int?)
(s/def :user-role/schema
  (s/keys :req [:user-role/user-id :user-role/role-id]))

