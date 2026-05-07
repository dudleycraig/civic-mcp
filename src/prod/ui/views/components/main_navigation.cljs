(ns ui.views.components.main-navigation
  (:require
   ["@heroicons/react/24/solid" :as solid-icons-24]
   [cljs.pprint]
   [reagent.core]
   [datascript.core]
   [reitit.frontend.easy]
   [ui.routes.pages]))

(defn nav-route
  [match-name {route-name :name route-label :label route-icon :icon}]
  [:li
   [:a.whitespace-nowrap
    {:href (reitit.frontend.easy/href route-name {})
     :class (when (= match-name route-name) "active")}
    (when route-icon [:> route-icon {:class "w-5 h-5"}])
    route-label]])

(defn render-nav-item
  [match-name {route-name :name route-label :label :as route}]
  (when (and route-label (not= route-name :ui.routes.pages/error))
    ^{:key (str route-name)}
    [nav-route match-name route]))

(defn view
  [{{[_root-path & routes] :router/routes route-state :route/state} :router :as props}]
  (let [{{route-name :name} :data} @route-state]
    [:nav
     [:ul.menu
      (dissoc props :router)
      (for [[_route-path route] routes]
        (render-nav-item route-name route))]]))
