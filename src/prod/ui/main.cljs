(ns main
     (:require
      [integrant.core]
      [reagent.core]
      [reagent.dom.client]
      [system.services]
      [goog.dom]))

   (defonce system (atom nil))
   (defonce profile :dev)
   (defonce web-root (reagent.dom.client/create-root (goog.dom/getElement "web")))

   (defn init
     []
     ;; Note: Adjusted check to match your specific cache file if needed, or generic halt
     (when @system (integrant.core/halt! @system))
     (let [new-system (system.services/init profile)]
       (reset! system new-system)
       (reagent.dom.client/render web-root [(:system.view/service new-system)])))

   (defn ^:dev/before-load before-load
     []
     (. js/console clear))

   (defn ^:dev/after-load after-load
     []
     (init))
