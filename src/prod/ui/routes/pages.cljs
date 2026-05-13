(ns ui.routes.pages
  (:require
   [integrant.core]
   [reagent.core]
   [clojure.string]
   [reitit.frontend.easy]
   [clojure.spec.alpha]
   ["@heroicons/react/24/solid" :as solid-icons-24]
   [datascript.core]
   [common.specs.user]

   [ui.controllers.authorization]
   [ui.controllers.login]
   [ui.controllers.logout]

   [ui.views.pages.login]
   [ui.views.pages.error]
   [ui.views.pages.home]
   [ui.views.pages.about]
   [ui.views.pages.contact]))

(defn get-routes [api session domain]
  ["/"

   ;; private routes
   [""
    {:controllers [(ui.controllers.logout/controller api session)]
     :layout :private}

    [""
     {:name ::home
      :controllers [(ui.controllers.authorization/controller session domain)]
      :view ui.views.pages.home/view
      :label "Home"
      :icon solid-icons-24/HomeIcon
      :roles ["administrator"]}]

    ["about"
     {:name ::about
      :controllers [(ui.controllers.authorization/controller session domain)]
      :view ui.views.pages.about/view
      :label "About"
      :icon solid-icons-24/IdentificationIcon
      :roles ["administrator"]}]

    ["contact"
     {:name ::contact
      :controllers [(ui.controllers.authorization/controller session domain)]
      :view ui.views.pages.contact/view
      :label "Contact"
      :icon solid-icons-24/AtSymbolIcon
      :roles ["administrator"]}]]

   ;; public routes
   ["login"
    {:name ::login
     :controllers [(ui.controllers.login/controller api session domain)]
     :view ui.views.pages.login/page
     :roles []
     :layout :public}]

   ["error"
    {:name ::error
     :view ui.views.pages.error/view
     :label "Error"
     :icon solid-icons-24/ExclamationCircleIcon
     :roles []
     :layout :public}]])




