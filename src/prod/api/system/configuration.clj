(ns api.system.configuration
  (:require
   [integrant.core]
   [clojure.tools.logging]
   [clojure.java.io]
   [aero.core]))

(defmethod integrant.core/init-key ::service
  [_ {profile :profile}]
  (clojure.tools.logging/info "Initializing Configuration Service for profile  \"" profile "\" ...")
  (-> "config.edn"
      (clojure.java.io/resource)
      (aero.core/read-config {:profile profile})))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  nil)
