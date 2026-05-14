(ns ui.views.components.main-navigation
  (:require
   [clojure.walk]
   [cljs.pprint]
   [reagent.core]
   [datascript.core]
   [reitit.frontend.easy]
   ["@heroicons/react/24/solid" :as solid-icons-24]
   [ui.utilities]
   [ui.routes.pages]))

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

(def device->class
  {:mobile "menu-sm dropdown-content mt-3 z-[1] p-2 shadow bg-base-100 rounded-box w-52"
   :desktop "menu-horizontal px-1"})

(defn nav-link
  [{{route-name :name route-label :label route-icon :icon} :route active? :active?}]
  [:li
   [:a.whitespace-nowrap
    {:href (reitit.frontend.easy/href route-name {})
     :class (when active? "menu-active")}
    (when route-icon [:> route-icon {:class "w-5 h-5"}])
    route-label]])

(defn nav-item
  [match-name {route-name :name route-label :label :as route}]
  (when (and route-label (not= route-name :ui.routes.pages/error))
    ^{:key (str route-name)}
    [nav-link {:route route :active? (= match-name route-name)}]))

(defn menu
  [{routes :routes match-name :match-name device :device :as props}]
  [:nav
   [:ul.menu
    (-> props
        (select-keys [:id :class :style])
        (update :class str " " (get device->class device)))
    (for [[_route-path route] routes]
      (nav-item match-name route))]])

(defn get-main-routes [routes session]
  (-> routes 
      (ui.utilities/get-routes-by-key-value :layout :main)
      (ui.utilities/get-authorized-routes session)))

(defn view
  [{routes :routes session :session match-name :match-name}]
  (let [
        authorized-routes (get-main-routes routes session)]
    [:<>
     [:div.navbar-start
      [:div.dropdown
       [:div.btn.btn-ghost.lg:hidden {:tab-index "0" :role "button" :aria-label "Toggle Navigation Menu"}
        [svg]]
       [menu {:routes authorized-routes :match-name match-name :device :mobile}]]
      [:div.btn.btn-ghost.text-xl.font-black company-text]]

     [:div.navbar-center.hidden.lg:flex
      {:class "lg:flex"}
      [menu {:routes authorized-routes :match-name match-name :device :desktop}]]]))





