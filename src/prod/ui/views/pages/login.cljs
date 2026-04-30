(ns ui.views.pages.login
  (:require
   [reagent.core]
   [clojure.spec.alpha]
   [reitit.frontend.easy]
   [common.specs.user]
   [clojure.string]
   [ui.views.forms.login]))

(defn page
  [{state :state}]
  (fn []
    [:section.min-h-screen.flex.items-center.justify-center.bg-base-300
     [:div.card.w-96.bg-base-100.shadow-xl
      [:div.card-body
       [:header.text-center.space-y-2
        [:h1.card-title.justify-center.text-2xl.font-black "CIVIC ZA"]
        [:p.text-sm
         {:class "text-base-content/60"}
         "Please sign in to your account"]]
       [ui.views.forms.login/form {:state state}]]]]))


