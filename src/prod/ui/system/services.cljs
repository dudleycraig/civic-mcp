(ns system.services
  (:require
   [integrant.core :refer [ref]]
   [system.configuration]
   [system.cache]
   [system.state]
   [system.router]
   [system.view]))

(defn create-system
  [profile]
  {:system.configuration/service {:profile profile}
   :system.cache/service         {:configuration (ref :system.configuration/service)}
   :system.state/service         {:configuration (ref :system.configuration/service)}
   :system.router/service        {:configuration (ref :system.configuration/service)
                                  :state (ref :system.state/service)}
   :system.view/service          {:configuration (ref :system.configuration/service)
                                  :state (ref :system.state/service)
                                  :router (ref :system.router/service)
                                  :cache  (ref :system.cache/service)}})

(defn init
  [profile]
  (integrant.core/init
   (create-system profile)))
