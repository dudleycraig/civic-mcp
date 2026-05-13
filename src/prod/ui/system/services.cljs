(ns ui.system.services
  (:require
   [integrant.core :refer [ref] :rename {ref iref}]
   [ui.system.configuration]
   [ui.system.cache]
   [ui.system.domain]
   [ui.system.session]
   [ui.system.router]
   [ui.system.api]
   [ui.system.view]))

(defn create-system
  [profile]
  {:ui.system.configuration/service {:profile profile}
   :ui.system.cache/service         {:configuration   (iref :ui.system.configuration/service)}
   :ui.system.domain/service        {:configuration   (iref :ui.system.configuration/service)}
   :ui.system.api/service           {:configuration   (iref :ui.system.configuration/service)}
   :ui.system.session/service       {:api             (iref :ui.system.api/service)}
   :ui.system.router/service        {:api             (iref :ui.system.api/service)
                                     :session         (iref :ui.system.session/service)
                                     :domain          (iref :ui.system.domain/service)}
   :ui.system.view/service          {:configuration   (iref :ui.system.configuration/service)
                                     :api             (iref :ui.system.api/service)
                                     :session         (iref :ui.system.session/service)
                                     :router          (iref :ui.system.router/service)}})

(defn init
  [profile]
  (integrant.core/init
   (create-system profile)))
