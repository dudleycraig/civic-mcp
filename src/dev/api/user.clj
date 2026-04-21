(ns user
  "```bash source ./bin/env.sh && PROFILE=dev clj -X:prod-api:dev-api:conjure ```"
  (:require
   [integrant.core]
   [integrant.repl]
   [integrant.repl.state]
   [api.system.services]
   [common.schemas]
   [common.entities]))

(integrant.repl/set-prep!
  (fn []
    (api.system.services/create-system :dev)))

(defmethod
  integrant.core/resume-key
  :api.system.database/service
  [key opts _old-opts _old-impl]
  (integrant.core/init-key key opts))

(defn provision!
  []
  (if-let [database (get integrant.repl.state/system :api.system.database/service)]
    (let [transact-worker (get-in database [:workers :transact])]
      (common.schemas/provision! transact-worker)
      (common.entities/provision! transact-worker)
      :done)
    (println "Database service not found in system state.")))

(defn db []
  (get integrant.repl.state/system :api.system.database/service))

#_(defn query
  [q & args]
  (let [query-fn (get-in (db) [:workers :query])]
    (apply query-fn q args)))

(comment
  (-> "PROFILE" System/getenv keyword)

  ;; start system
  (integrant.repl/go)

  ;; provision system after start
  (provision!)

  ;; stop system
  (integrant.repl/halt)

  ;; restart system
  (integrant.repl/reset)

  ;; restart all systems
  (integrant.repl/reset-all))



