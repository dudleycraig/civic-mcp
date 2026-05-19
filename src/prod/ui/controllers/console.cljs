(ns ui.controllers.console
  (:require
   [reagent.core]
   [ui.utilities]))

(defn controller
  [api database]
  (let [state (reagent.core/atom {:status :inert :wards []})]
    {:name      ::controller
     :state     state
     :identity  (fn [match] match)
     :start     (fn [match] nil)
     :stop      (fn [match]
                  (reset! state {:status :inert :wards []}))}))

