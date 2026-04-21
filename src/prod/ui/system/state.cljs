(ns system.state
  (:require
   [integrant.core]))

(defn init
  [configuration]
  {})

(defn halt
  []
  nil)

(defmethod integrant.core/init-key ::service
  [_ {configuration :configuration}]
  (init configuration))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  (halt))

