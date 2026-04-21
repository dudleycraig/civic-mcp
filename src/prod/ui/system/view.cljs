(ns system.view
  (:require
   [integrant.core]))

(defn init
  [configuration state router cache]
  {})

(defn halt
  []
  nil)

(defmethod integrant.core/init-key ::service
  [_ {configuration :configuration
      state :state
      router :router
      cache :cache}]
  (init configuration state router cache))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  (halt))

