(ns ui.main
  (:require
   [integrant.core]
   [reagent.core]
   [reagent.dom.client]
   [goog.dom]
   [ui.system.services]
   [ui.system.view]))

(goog-define profile "")

(defonce system (atom nil))
(defonce ui-root (reagent.dom.client/create-root (goog.dom/getElement "ui-root")))

(defn init
  []
  (when @system (integrant.core/halt! @system))
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
