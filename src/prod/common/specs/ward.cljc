(ns common.specs.ward
  (:require [clojure.spec.alpha :as s]
            [common.regex]))

(s/def :ward/id int?)
(s/def :ward/code int?)
(s/def :ward/number string?)

(s/def :ward/min-x int?)
(s/def :ward/min-y int?)
(s/def :ward/max-x int?)
(s/def :ward/max-y int?)

(s/def :ward/geometry-type keyword?)
(s/def :ward/coordinates-json string?)

(s/def :ward/schema
  (s/keys :req [:ward/id
                :ward/code
                :ward/number
                :ward/geometry-type
                :ward/coordinates-json]
          :opt [:ward/min-x
                :ward/min-y
                :ward/max-x
                :ward/max-y]))
