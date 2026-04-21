(ns api.system.router
  (:require
   [integrant.core]
   [clojure.tools.logging]
   [reitit.ring]
   [reitit.ring.coercion]
   [reitit.ring.middleware.muuntaja]
   [muuntaja.core]
   [ring.middleware.cookies]
   [api.routes]
   [api.system.database]))

(defmethod integrant.core/init-key ::service
  [_ {configuration :configuration database :database authentication :authentication _mcp :mcp}]
  (clojure.tools.logging/info "Initializing router service ...")
  (->
   (reitit.ring/ring-handler
    (reitit.ring/router
     (api.routes/get-routes configuration database authentication)
     {:data {:muuntaja muuntaja.core/instance}}))
   ring.middleware.cookies/wrap-cookies))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  nil)




