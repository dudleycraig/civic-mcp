(ns ui.views.pages.console
  (:require
   [reagent.core]))

(defn ward-row
  [{:ward/keys [code number id]}]
  [:tr
   [:td id]
   [:td code]
   [:td number]])

(defn view
  [{{{{{state :state} :ui.controllers.console/controller} :controllers} :data} :match}]
  [:section.space-y-6
   [:header
    [:h1.text-4xl.font-black.tracking-tight "Console"]
    [:p
     {:class "text-base-content/60"}
     "Map Console"]]

   [:div.card.bg-base-100.shadow-xl
    [:div.card-body
     [:h2.card-title "Wards"
      (when (= (:status @state) :loading)
        [:span.loading.loading-spinner.loading-sm])]
     (cond
       (= (:status @state) :ready)
       [:div.overflow-x-auto
        [:table.table.table-zebra.table-sm
         [:thead
          [:tr [:th "ID"] [:th "Code"] [:th "Number"]]]
         [:tbody
          (for [ward (:data @state)]
            ^{:key (:ward/id ward)}
            [ward-row ward])]]]

       (= (:status @state) :error)
       [:div.alert.alert-error "Failed loading wards"]

       :else 
       [:div.py-10.text-center "Fetching wards ..."])]]

   [:div.grid.grid-cols-1.lg:grid-cols-2.gap-6
    [:section.card.bg-base-100.shadow-xl
     [:div.card-body
      [:h2.card-title "Correspondence"]
      [:div.aspect-video.bg-base-300.rouded-lg.flex.items-center.justify-center
       "Form placeholder"]]]]])
