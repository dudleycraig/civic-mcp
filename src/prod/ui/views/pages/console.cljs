(ns ui.views.pages.console
  (:require
   [reagent.core]
   ["civic-za-diorama" :default Diorama]))

(defn view
  [{{{{{console-state :state} :ui.controllers.console/controller} :controllers} :data} :match}]
  (reagent.core/with-let [data (reagent.core/track #(clj->js @console-state))]
    [:div.w-full.h-screen
     [:> Diorama
      {:data    @data
       :options #js {:theme "dark" :wireframe false}}]]))




