(ns common.entities.utilities
  (:require
   [datomic.client.api]
   [clj-uuid.core]
   #? (:clj [buddy.hashers])))

(defn generate-hash
  "one-way hashing of password"
  [password]
  #? (:clj (buddy.hashers/derive password {:alg :bcrypt+blake2b-512})))

(defn generate-uuid
  "generate a v7 uuid"
  []
  #? (:clj (clj-uuid.core/v7)))

(defn get-previous-id
  "fetches current maximum :$entity/id"
  [query-worker id-key]
  (or
   (ffirst
    (query-worker
     '[:find (max ?id)
       :in $ ?id-key
       :where [_ ?id-key ?id]]
     id-key))
   0))

