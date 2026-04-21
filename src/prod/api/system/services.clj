(ns api.system.services
  (:require
   [integrant.core :refer [ref] :rename {ref iref}]
   [api.system.configuration]
   [api.system.cache]
   [api.system.database]
   [api.system.authentication]
   [api.system.mcp]
   [api.system.router]
   [api.system.http]))

(defn create-system
  [profile]
  {:api.system.configuration/service  {:profile profile}
   :api.system.cache/service          {:configuration   (iref :api.system.configuration/service)}
   :api.system.database/service       {:configuration   (iref :api.system.configuration/service)}
   :api.system.authentication/service {:configuration   (iref :api.system.configuration/service)
                                       :database        (iref :api.system.database/service)}
   :api.system.mcp/service            {:configuration   (iref :api.system.configuration/service)
                                       :database        (iref :api.system.database/service)}
   :api.system.router/service         {:configuration   (iref :api.system.configuration/service)
                                       :database        (iref :api.system.database/service)
                                       :authentication  (iref :api.system.authentication/service)
                                       :mcp             (iref :api.system.mcp/service)}
   :api.system.http/service           {:configuration   (iref :api.system.configuration/service)
                                       :router          (iref :api.system.router/service)}})

(defn init
  [profile]
  (integrant.core/init
   (create-system profile)))




