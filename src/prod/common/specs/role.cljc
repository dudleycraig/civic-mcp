(ns common.specs.role
  (:require [clojure.spec.alpha]
            [common.regex]))

(clojure.spec.alpha/def :role/name (clojure.spec.alpha/or :keyword keyword? :string string?))
(clojure.spec.alpha/def :role/description string?)

(clojure.spec.alpha/def :role/ref
  (clojure.spec.alpha/tuple #{:role/name} :role/name))

(clojure.spec.alpha/def :role/spec
  (clojure.spec.alpha/or
   :map (clojure.spec.alpha/keys :req [:role/name]
                                 :opt [:role/description])
   :ref :role/ref))
