(ns common.specs.user-site
  (:require [clojure.spec.alpha :as s]
            [common.regex]))

(s/def :user-site/user-id int?)
(s/def :user-site/site-id int?)
(s/def :user-site/schema
  (s/keys :req [:user-site/user-id :user-site/site-id]))

