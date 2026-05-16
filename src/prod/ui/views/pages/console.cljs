(ns ui.views.pages.console
  (:require
   [reagent.core]
   ["civic-za-diorama" :default Diorama]))

(defn view
  [{{{{{console-state :state} :ui.controllers.console/controller} :controllers} :data} :match}]
  [:div.w-full.h-screen
   [:> Diorama
    {:data    (-> console-state deref clj->js)
     :options (clj->js {:theme "dark" :wireframe false})}]])
