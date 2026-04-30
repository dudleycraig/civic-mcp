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

   [ui.controllers.login]

   [ui.views.pages.login]
   [ui.views.pages.error]
   [ui.views.pages.home]
   [ui.views.pages.about]
   [ui.views.pages.contact]))

(defn get-routes [configuration state]
  [["login"
    {:name ::login
     :view ui.views.pages.login/page
     :controllers [(ui.controllers.login/controller configuration state)]
     :layout :standalone}]

   ["error"
    {:name ::error
     :view ui.views.pages.error/view
     :label "Error"
     :icon solid-icons-24/ExclamationCircleIcon
     :layout :standalone}]

   [""
    {:name ::home
     :view ui.views.pages.home/view
     :label "Home"
     :icon solid-icons-24/HomeIcon
     :layout :standard}]

   ["about"
    {:name ::about
     :view ui.views.pages.about/view
     :label "About"
     :icon solid-icons-24/IdentificationIcon
     :layout :standard}]

   ["contact"
    {:name ::contact
     :view ui.views.pages.contact/view
     :label "Contact"
     :icon solid-icons-24/AtSymbolIcon
     :layout :standard}]])


