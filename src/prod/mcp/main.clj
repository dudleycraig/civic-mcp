(ns mcp.main
  (:require
   [integrant.core]
   [mcp.system.services]))

(defonce system (atom nil))

(defn -main
  [& _args]
  (let [profile (or (System/getenv "PROFILE") :dev)
        new-system (mcp.system.services/init (keyword profile))]
    (reset! system new-system)))
