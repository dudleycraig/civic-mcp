(ns cli.system.configuration
  (:require
   [aero.core]
   [clojure.java.io]))

;; TODO: assign profile via main or environment variable
(defonce config (-> "config.edn"
                    (clojure.java.io/resource)
                    (aero.core/read-config {:profile :dev})))

(defn extract 
  ([]
   config)
  ([& key-sequence]
   (get-in config key-sequence)))


