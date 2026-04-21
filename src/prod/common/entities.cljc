(ns common.entities
  (:require
   [clojure.spec.alpha]
   [common.entities.user]
   [common.entities.role]
   [common.entities.ward]))

(defn provision!
  [transact!]
  (transact! (clojure.spec.alpha/coll-of :role/spec :kind vector?) common.entities.role/entities)
  (transact! (clojure.spec.alpha/coll-of :user/private :kind vector?) common.entities.user/entities))




