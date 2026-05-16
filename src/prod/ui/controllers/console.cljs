(ns ui.controllers.console
  (:require
   [reagent.core]
   [common.entities.ward]
   [ui.utilities]))

(defn fetch-wards!
  [api database state]
  (let [{transact-database-entities :transact/entities query-database :query} database]
    (swap! state assoc :status :loading)
    (-> ((:get-wards api))
        (.then (fn [response]
                 (if (. response -ok)
                   (. response json)
                   (throw (js/Error. "Failed fetching wards")))))
        (.then (fn [json]
                 (let [wards (-> json js->clj ui.utilities/keywordize)]
                   (common.entities.ward/add! transact-database-entities wards)
                   (let [wards (->> (query-database `[:find (pull ?e [*]) :where [?e :ward/id]])
                                    (map first)
                                    (vec))]
                     (swap! state assoc :status :ready :wards wards)))))

        (.catch (fn [error]
                  (swap! state assoc :status :error :wards [])
                  (. js/console log "Wards Error: " error))))))

(defn controller
  [api database]
  (let [state (reagent.core/atom {:status :inert :wards []})]
    {:name      ::controller
     :state     state
     :identity  (fn [match] match)
     :start     (fn [match] (fetch-wards! api database state))
     :stop      (fn [match]
                  (reset! state {:status :inert :wards []}))}))


