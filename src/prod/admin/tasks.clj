(ns admin.tasks
  (:require
   [clojure.data.json]
   [datomic.client.api]
   [common.schemas.role]
   [common.schemas.user]
   [common.schemas.ward]
   [admin.system.configuration]))

(defn get-client
  []
  (->
    ;; TODO: system.configuration/extract adds a tightly coupled dependency, rather pass as parameters 
   (admin.system.configuration/extract :datomic :client)
   (datomic.client.api/client)))

(defn create-database
  [datomic-client]
  (->
    ;; TODO: system.configuration/extract adds a tightly coupled dependency, rather pass as parameters 
   (admin.system.configuration/extract :datomic :db-name)
   (hash-map :db-name)
   (datomic.client.api/create-database)))

(defn get-client-conn
  [datomic-client]
  (->>
    ;; TODO: system.configuration/extract adds a tightly coupled dependency, rather pass as parameters 
   (admin.system.configuration/extract :datomic :db-name)
   (hash-map :db-name)
   (datomic.client.api/connect datomic-client)))

(defn get-schemas
  []
  (concat
   common.schemas.role/schema
   common.schemas.user/schema
   common.schemas.ward/schema))

(defn ward-seed-data
  "slurps geojson file (resources/data/WARDS.ESRI102562.geojson)
   and formats as entities
   according to the schema at src/core/common/schemas/ward.cljc"
  [conn geojson-path])

(def seed-data
  [{:role/id 1
    :role/name "ADMINISTRATOR"
    :role/description "Create, read, update and delete all aspects of application"}
   {:role/id 2
    :role/name "GUEST"
    :role/description "Can only read certain aspects of application"}])

(defn init-db
  []
  (println "initializing db")
  (let [client (get-client)
        conn (get-client-conn client)]
    (when (create-database client)
      (println "Transacting Schemas...")
      (datomic.client.api/transact conn {:tx-data (get-schemas)})
      (println "Transacting Seed Data...")
      (datomic.client.api/transact conn {:tx-data seed-data})
      (println "Database initialization complete."))))

(defn reset-db
  "WARNING: Deletes and recreates the database.
      Usage: clj -X:db-reset"
  []
  (println "WARNING: Resetting Database...")
  (let [datomic-client (get-client)]
    (->>
    ;; TODO: system.configuration/extract adds a tightly coupled dependency, rather pass as parameters 
     (admin.system.configuration/extract :datomic :db-name)
     (hash-map :db-name)
     (datomic.client.api/delete-database datomic-client))
    (init-db)))






