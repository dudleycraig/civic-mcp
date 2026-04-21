(ns api.main
  "```bash source ./bin/env.sh && PROFILE=prod clj -X:prod-api ```"
  (:require
   [integrant.core]
   [api.system.services]))

(defonce system (atom nil))

(defn -main
  [& _args]
  (let [profile (-> (System/getenv "PROFILE") keyword)]
    (when-not (contains? #{:dev :test :prod} profile)
      (throw
       (ex-info
        (str "Failed reading $PROFILE environment variable, found \"" profile "\".")
        {::-main {:status :error :code 500}})))
    (reset! system (api.system.services/init profile))))

(comment
  (do
    (require '[integrant.repl])
    (integrant.repl/go))

  (do
    (require '[integrant.repl])
    (integrant.repl/reset))

  (do
    (require '[integrant.repl])
    (integrant.repl/halt)))



