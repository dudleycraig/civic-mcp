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
   [ui.controllers.console]

   [ui.views.pages.login]
   [ui.views.pages.error]
   [ui.views.pages.home]
   [ui.views.pages.about]
   [ui.views.pages.contact]
   [ui.views.pages.console]))

(defn get-routes [api session database]
  ["/"
   {:controllers [(ui.controllers.logout/controller api session)]
    :layout :main}

   [""
    {:name ::home
     :controllers [(ui.controllers.authorization/controller session database)]
     :view ui.views.pages.home/view
     :label "Home"
     :icon solid-icons-24/HomeIcon
     :roles []}]

   ["about"
    {:name ::about
     :controllers [(ui.controllers.authorization/controller session database)]
     :view ui.views.pages.about/view
     :label "About"
     :icon solid-icons-24/IdentificationIcon
     :roles []}]

   ["contact"
    {:name ::contact
     :controllers [(ui.controllers.authorization/controller session database)]
     :view ui.views.pages.contact/view
     :label "Contact"
     :icon solid-icons-24/AtSymbolIcon
     :roles ["administrator"]}]

   ["console"
    {:name ::console
     :controllers [(ui.controllers.authorization/controller session database)
                   (ui.controllers.console/controller api database)]
     :view ui.views.pages.console/view
     :label "Console"
     :icon solid-icons-24/AtSymbolIcon
     :roles ["administrator"]}]

   ["login"
    {:name ::login
     :controllers [(ui.controllers.login/controller api session database)]
     :view ui.views.pages.login/page
     :roles []}]

   ["error"
    {:name ::error
     :view ui.views.pages.error/view
     :label "Error"
     :icon solid-icons-24/ExclamationCircleIcon
     :roles []}]])




