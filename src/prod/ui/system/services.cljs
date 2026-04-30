(ns ui.system.services
  (:require
   [integrant.core :refer [ref] :rename {ref iref}]
   [ui.system.configuration]
   [ui.system.cache]
   [ui.system.state]
   [ui.system.router]
   [ui.system.view]))

(defn create-system
  [profile]
  {:ui.system.configuration/service {:profile profile}
   :ui.system.cache/service         {:configuration   (iref :ui.system.configuration/service)}
   :ui.system.state/service         {:configuration   (iref :ui.system.configuration/service)}
   :ui.system.router/service        {:configuration   (iref :ui.system.configuration/service)
                                     :state           (iref :ui.system.state/service)}
   :ui.system.view/service          {:configuration   (iref :ui.system.configuration/service)
                                     :state           (iref :ui.system.state/service)
                                     :router          (iref :ui.system.router/service)}})

(defn init
  [profile]
  (integrant.core/init
   (create-system profile)))
