(ns system.router
  (:require
   [integrant.core]))

(defn init
  [configuration state]
  {})

(defn halt
  []
  nil)

(defmethod integrant.core/init-key ::service
  [_ {configuration :configuration
      state :state}]
  (init configuration state))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  (halt))

