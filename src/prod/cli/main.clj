(ns cli.main
  (:require
   [cli.tasks]))

(defn help
  []
  (println "Tasks Available:")
  (println "  :init-db      - Initialize database")
  (println "  :reset-db     - Reset database"))

(defn init [{action :action}]
  (case action
    :init-db (cli.tasks/init-db)
    :reset-db (cli.tasks/reset-db)
    (help)))
