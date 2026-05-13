(ns ui.views.components.main-header
  (:require
   [reagent.core]
   ["@heroicons/react/24/solid" :as solid-icons-24]
   [ui.views.components.main-navigation]))

(def company-text "CIVIC ZA")

(defn svg []
  [:svg
   {:xmlns "http://www.w3.org/2000/svg"
    :fill "none"
    :viewBox "0 0 24 24"
    :class "inline-block h-6 w-6 stroke-current"}
   [:path
    {:stroke-linecap "round"
     :stroke-linejoin "round"
     :stroke-width "2"
     :d "M4 6h16M4 12h16M4 18h16"}]])

(defn view
  [{user-email :user-email on-logout :on-logout match-name :match-name routes :routes}]
  [:header.flex-none.bg-base-200.shadow-md
   {:role "banner"}
   [:nav.navbar.px-4
    {:aria-label "Primary Navigation"}

    [:div.navbar-start
     [:div.dropdown
      [:div.btn.btn-ghost.lg:hidden
       {:tab-index "0" :role "button" :aria-label "Toggle Navigation Menu"}
       [svg]]
      [ui.views.components.main-navigation/view
       {:routes routes :match-name match-name :device :mobile}]]
     [:div.btn.btn-ghost.text-xl.font-black company-text]]

    [:div.navbar-center.hidden.lg:flex
     {:class "lg:flex"}
     [ui.views.components.main-navigation/view
      {:routes routes :match-name match-name :device :desktop}]]

    [:div.navbar-end
     (if user-email
       [:div.flex.items-center.gap-x-2
        [:span.text-sm.font-medium.text-success user-email]
        [:button.btn.btn-outline.btn-sm
         {:on-click on-logout
          :aria-label "Logout From Session"}
         "LOGOUT"]]
       [:span.text-sm.font-medium.text-error "Invalid Session"])]]])




