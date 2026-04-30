(ns ui.system.state
  (:require
   [reagent.core]
   [integrant.core]))

(defn init
  [configuration]
  (reagent.core/atom {}))

(defn halt
  []
  nil)

(defmethod integrant.core/init-key ::service
  [_ {configuration :configuration}]
  (init configuration))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  (halt))

