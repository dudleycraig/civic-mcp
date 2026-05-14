(ns ui.views.components.main-header
  (:require
   [reagent.core]
   [reitit.frontend.easy]
   ["@heroicons/react/24/solid" :as solid-icons-24]))

(defn view
  [{user-email :user-email on-logout :on-logout} & children]
  [:header.flex-none.bg-base-200.shadow-md
   {:role "banner"}
   [:nav.navbar.px-4
    {:aria-label "Private Navigation"}
    [:<> children]
    [:div.navbar-end
     (if user-email
       [:div.flex.items-center.gap-x-2
        [:span.text-sm.font-medium.text-success user-email]
        [:a.btn.btn-outline.btn-sm
         {:on-click on-logout
          :aria-label "Logout From Session"}
         "LOGOUT"]]
       [:div.flex.items-center.gap-x-2
        [:span.text-sm.font-medium.text-success user-email]
        [:a.btn.btn-outline.btn-sm
         {:href :ui.routes.pages/login
          :aria-label "Logout From Session"}
         "LOGIN"]])]]])




