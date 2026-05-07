(ns ui.views.pages.about)

(defn view
  [props]
  [:section.space-y-6
   [:header
    [:h1.text-4xl.font-black.tracking-tight "About"]
    [:p
     {:class "text-base-content/60"}
     "About CIVIC ZA"]]
   [:div.grid.grid-cols-1.lg:grid-cols-2.gap-6
    [:section.card.bg-base-100.shadow-xl
     [:div.card-body
      [:h2.card-title "CIVIC ZA Details"]
      [:div.aspect-video.bg-base-300.rouded-lg.flex.items-center.justify-center
       "Details"]]]]])
