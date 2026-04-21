(ns common.entities.role
  (:require
   [clojure.spec.alpha]
   [common.specs.role]))

(def administrator-role
  {:role/name         :administrator
   :role/description  "Create, read, update and delete all aspects of application"})

(def guest-role
  {:role/name         :guest
   :role/description  "Can only read certain aspects of application"})

(def entities
  [administrator-role
   guest-role])

(defn add!
  "add roles"
  [transact-worker! & entities]
  (transact-worker! (clojure.spec.alpha/coll-of :role/schema :kind vector?) entities))

(defn fetch-all
  [query-worker]
  (query-worker '[:find (pull ?e [*])
                  :in $
                  :where [?e :role/name]]))




