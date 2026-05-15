(ns ui.system.database
  (:require
   [reagent.core]
   [integrant.core]
   [common.schemas]
   [clojure.spec.alpha]
   [datascript.core]))

(defn transact-schemas-handler
  [database-state schema]
  (swap! database-state update :schema merge schema))

(defn transact-entities-handler
  ([database-state data]
   (datascript.core/transact! database-state data))
  ([database-state spec data]
   (if (clojure.spec.alpha/valid? spec data)
     (transact-entities-handler database-state data)
     (throw (js/Error. (clojure.spec.alpha/explain-data spec data))))))

(defn query-handler
  [database-state query & args]
  (let [data (deref database-state)]
    (apply datascript.core/q query data args)))

(defmethod integrant.core/init-key ::service
  [_ _]
  (let [state (datascript.core/create-conn)]
    (common.schemas/provision! (partial transact-schemas-handler state))
    {:state             state
     :transact/schemas  (partial transact-schemas-handler state)
     :transact/entities (partial transact-entities-handler state)
     :query             (partial query-handler state)}))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  (fn [] nil))

