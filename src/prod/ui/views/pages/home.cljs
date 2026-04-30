(ns ui.views.pages.home)

(defn view
  []
  [:section.space-y-6
   [:header
    [:h1.text-4xl.font-black.tracking-tight "Home"]
    [:p
     {:class "text-base-content/60"}
     "Main Interface"]]
   [:div.grid.grid-cols-1.lg:grid-cols-2.gap-6
    [:section.card.bg-base-100.shadow-xl
     [:div.card-body
      [:h2.card-title "Map View"]
      [:div.aspect-video.bg-base-300.rouded-lg.flex.items-center.justify-center
       "3D Map"]]]]])
