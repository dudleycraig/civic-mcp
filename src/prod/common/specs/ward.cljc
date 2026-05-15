(ns common.specs.ward
  (:require [clojure.spec.alpha :as s]
            [common.regex]))

(s/def :ward/id int?)
(s/def :ward/code int?)
(s/def :ward/number string?)
(s/def :ward/gav-primary int?)

(s/def :ward/min-x number?)
(s/def :ward/min-y number?)
(s/def :ward/max-x number?)
(s/def :ward/max-y number?)

(s/def :ward/geometry-type keyword?)
(s/def :ward/coordinates-json string?)

(s/def :ward/spec
  (s/keys :req [:ward/id
                :ward/code
                :ward/number
                :ward/geometry-type
                :ward/min-x
                :ward/min-y
                :ward/max-x
                :ward/max-y]
          :opt [:ward/gav-primary
                :ward/simplified
                :ward/coordinates-json]))
