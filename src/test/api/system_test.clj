(ns api.system-test
  (:require
   [integrant.core]
   [api.system.services]
   [common.schemas]
   [common.entities]))

(defonce system (atom nil))

(defn start!
  "Starts the system for the :test profile."
  []
  (when-not (deref system)
    (let [new-system (api.system.services/init :test)]
      (reset! system new-system)
      (let [{{transact-worker! :transact query-worker :query} :workers} (get new-system :api.system.database/service)]
        (common.schemas/provision! transact-worker!)
        (common.entities/provision! transact-worker!)
        #_(let [roles (query-worker '[:find (pull ?e [:role/name :role/description])
                                      :where [?e :role/name]])]
            (println "provisioned roles ..." roles))))))

(defn stop!
  "Halts the system and clears the state."
  []
  (when (deref system)
    (integrant.core/halt! (deref system))
    (reset! system nil)))

(defn get-configuration
  "returns static configuration from configuration service."
  []
  (get @system :api.system.configuration/service))

(defn get-database
  "returns database (datomic connection and workers) from database service."
  []
  (get @system :api.system.database/service))

(defn get-router
  "returns router (ring-handler) from router service."
  []
  (get @system :api.system.router/service))

(defn get-handler
  "returns ring-handler from router service."
  []
  (api.system-test/get-router))

(comment
  (require 'api.system-test :reload)
  (api.system-test/start!)
  (api.system-test/stop!))





