(ns common.schemas
  (:require
   [clojure.spec.alpha]
   [common.schemas.role]
   [common.schemas.user]
   [common.schemas.ward]))

(defn provision!
  [transact!]
  (transact!
   (concat
    common.schemas.role/schema
    common.schemas.user/schema
    common.schemas.ward/schema)))




