(ns admin.main
  (:require
   [admin.tasks]))

(defn help
  []
  (println "Tasks Available:")
  (println "  :init-db      - Initialize database")
  (println "  :reset-db     - Reset database"))

(defn init [{action :action}]
  (case action
    :init-db (admin.tasks/init-db)
    :reset-db (admin.tasks/reset-db)
    (help)))
