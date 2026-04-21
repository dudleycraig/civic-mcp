(ns api.system.database
  (:require
   [integrant.core]
   [datomic.client.api]
   [clojure.tools.logging]
   [clojure.spec.alpha]
   [clojure.pprint]
   [common.schemas]
   [common.entities]))

(defn transact!
  "transacts data into database. optionally pass a spec to validate data."
  ([connection data]
   (datomic.client.api/transact connection {:tx-data data}))
  ([connection spec data]
   (if (clojure.spec.alpha/valid? spec data)
     (transact! connection data)
     (throw
      (ex-info
       "Transact Error"
       {::transact!
        {:message/status :error
         :message/code 500
         :message/data (clojure.spec.alpha/explain spec data)}})))))

(defn query
  "query against database snapshot value"
  [connection query & args]
  (let [value (datomic.client.api/db connection)]
    (datomic.client.api/q {:query query :args (vec (cons value args))})))

(defmethod integrant.core/init-key ::service
  [_ {{{{{client-config :client db-name :db-name} :datomic} :database} :api :as configuration} :configuration}]
  (clojure.tools.logging/info "Initializing datomic peer client ...")
  (let [client (datomic.client.api/client client-config)]
    (when (= (:server-type client-config) :datomic-local)
      (clojure.tools.logging/info "Provisioning test database ...")
      (datomic.client.api/create-database client {:db-name db-name}))
    (let [connection (datomic.client.api/connect client {:db-name db-name})]
      {:db-name     db-name
       :client      client
       :connection  connection
       :workers     {:transact (partial api.system.database/transact! connection)
                     :query    (partial query connection)}})))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  (clojure.tools.logging/info "Halting Database Service...")
  nil)
