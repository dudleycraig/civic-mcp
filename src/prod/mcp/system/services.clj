(ns mcp.system.services
  (:require
   [integrant.core :refer [ref] :rename {ref iref}]
   [mcp.system.configuration]
   [mcp.system.mcp]))

(defn create-system
  [profile]
  {:mcp.system.configuration/service {:profile profile}
   :mcp.system.mcp/service           {:configuration (iref :system.configuration/service)}})

(defn init
  [profile]
  (integrant.core/init
   (create-system profile)))
