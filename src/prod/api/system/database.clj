(ns api.system.database
  (:require
   [integrant.core]
   [datomic.client.api]
   [clojure.tools.logging]
   [clojure.spec.alpha]
   [clojure.pprint]
   [common.schemas]
   [common.entities]))

(defn transact-handler
  ([database-state data]
   (datomic.client.api/transact database-state {:tx-data data}))
  ([database-state spec data]
   (if (clojure.spec.alpha/valid? spec data)
     (transact-handler database-state data)
     (throw (Exception. (clojure.spec.alpha/explain-data spec data))))))

(defn query-handler
  [database-state query & args]
  (let [data (datomic.client.api/db database-state)]
    (datomic.client.api/q {:query query :args (vec (cons data args))})))

(defmethod integrant.core/init-key ::service
  [_ {{{{{client-config :client db-name :db-name} :datomic} :database} :api :as configuration} :configuration}]
  (clojure.tools.logging/info "Initializing datomic peer client ...")
  (let [client (datomic.client.api/client client-config)]
    (when (= (:server-type client-config) :datomic-local)
      (clojure.tools.logging/info "Provisioning test database ...")
      (datomic.client.api/create-database client {:db-name db-name}))
    (let [state (datomic.client.api/connect client {:db-name db-name})]
      {:state             state
       :db-name           db-name
       :client            client
       :transact/schemas  (partial transact-handler state)
       :transact/entities (partial transact-handler state)
       :query             (partial query-handler state)})))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  (clojure.tools.logging/info "Halting Database Service...")
  nil)



