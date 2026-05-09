(ns ui.views.components.main-navigation
  (:require
   ["@heroicons/react/24/solid" :as solid-icons-24]
   [cljs.pprint]
   [reagent.core]
   [datascript.core]
   [reitit.frontend.easy]
   [ui.routes.pages]))

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

(defn view
  [{routes :routes match-name :match-name device :device :as props}]
  [:nav
   [:ul.menu
    (-> props
        (select-keys [:id :class :style])
        (update :class str " " (get device->class device)))
    (for [[_route-path route] routes]
      (nav-item match-name route))]])




