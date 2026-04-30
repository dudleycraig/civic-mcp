(ns ui.views.components.shell
  (:require
   [reagent.core]))
(defn view
  [props & children]
  (into
   [:div.w-full.h-full.flex.flex-col
    (reagent.core/merge-props
     (dissoc props :router)
     {:role "application"
      :aria-label "CIVIC ZA"})]

   children))




