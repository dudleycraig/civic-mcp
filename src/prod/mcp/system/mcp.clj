(ns mcp.system.mcp
  (:require
   [integrant.core]))

(defn init
  [config]
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
