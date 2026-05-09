(ns ui.main
  (:require
   [integrant.core]
   [reagent.core]
   [reagent.dom.client]
   [goog.dom]
   [reitit.frontend.easy]
   [ui.system.services]
   [ui.system.view]))

(goog-define profile "")
(goog-define pwa? true)

(defonce system (atom nil))
(defonce ui-root (reagent.dom.client/create-root (goog.dom/getElement "ui-root")))

(defn toggle-pwa
  [pwa?]
  (if (and pwa? (exists? js/navigator.serviceWorker))
    (->
     js/navigator.serviceWorker
     (.register "/service-worker.js")
     (.then #(. js/console log "PWA Enabled")))
    (when (exists? js/navigator.serviceWorker)
      (->
       js/navigator.serviceWorker
       (.getRegistrations)
       (.then (fn [registrations] (doseq [registration (array-seq registrations)] (.unregister registration))))))))

(defn init
  []
  (when @system (integrant.core/halt! @system))
  (toggle-pwa pwa?)
  (->>
   (keyword profile)
   (ui.system.services/init)
   (reset! system)
   :ui.system.view/service
   (vector)
   (reagent.dom.client/render ui-root)))

(defn ^:dev/before-load before-load
  []
  (. js/console clear))

(defn ^:dev/after-load after-load
  []
  (init))




