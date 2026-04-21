(ns mcp.system.configuration
  (:require
   [integrant.core]
   [aero.core]
   [clojure.java.io]))

(defn init
  [profile]
  (-> "config.edn"
      (clojure.java.io/resource)
      (aero.core/read-config {:profile profile})))

(defn halt
  []
  nil)

(defmethod integrant.core/init-key ::service
  [_ {profile :profile}]
  (init profile))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  (halt))
