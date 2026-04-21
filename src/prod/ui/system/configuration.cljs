(ns system.configuration
  (:require
   [integrant.core]))

(defn init
  [profile]
  {})

(defn halt
  []
  nil)

(defmethod integrant.core/init-key ::service
  [_ {profile :profile}]
  (init profile))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  (halt))

