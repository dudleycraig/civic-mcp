(ns ui.views.components.main-header
  (:require
   [reagent.core]
   ["@heroicons/react/24/solid" :as solid-icons-24]))

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
  [[navbar-component navbar-props]]
  [:header.flex-none.bg-base-200.shadow-md
   {:role "banner"}
   [:nav.navbar.px-4
    {:aria-label "Primary Navigation"}

   ;; mobile navigation
    [:div.navbar-start
     [:div.dropdown
      [:div.btn.btn-ghost.lg:hidden
       {:tab-index "0" :role "button" :aria-label "Toggle Navigation Menu"}
       [svg]]
      [navbar-component
       (reagent.core/merge-props
        navbar-props
        {:class "menu-sm dropdown-content mt-3 z-[1] p-2 shadow bg-base-100 rounded-box w-52"})]]
     [:div.btn.btn-ghost.text-xl.font-black company-text]]

   ;; desktop navigation
    [:div.navbar-center.hidden.lg:flex
     {:class "lg:flex"}
     [navbar-component
      (reagent.core/merge-props navbar-props {:class "menu-horizontal px-1"})]]

    [:div.navbar-end
     [:button.btn.btn-outline.btn-sm
      {:aria-label "Logout From Session"}
      "LOGOUT"]]]])




