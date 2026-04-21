(ns api.system.mcp
  (:require
   [integrant.core]))

(defn init
  [configuration database]
  {})

(defn halt
  []
  nil)

(defmethod integrant.core/init-key ::service
  [_ {configuration :configuration
      database :database}]
  (init configuration database))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  (halt))
