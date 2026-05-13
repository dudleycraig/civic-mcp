(ns ui.views.components.main-header
  (:require
   [reagent.core]
   ["@heroicons/react/24/solid" :as solid-icons-24]
   [ui.views.components.main-navigation]))

(defn view
  [{user-email :user-email on-logout :on-logout} & children]
  [:header.flex-none.bg-base-200.shadow-md
   {:role "banner"}
   [:nav.navbar.px-4
    {:aria-label "Primary Navigation"}
    [:<> children]
    [:div.navbar-end
     (if user-email
       [:div.flex.items-center.gap-x-2
        [:span.text-sm.font-medium.text-success user-email]
        [:button.btn.btn-outline.btn-sm
         {:on-click on-logout
          :aria-label "Logout From Session"}
         "LOGOUT"]]
       [:span.text-sm.font-medium.text-error "Invalid Session"])]]])




