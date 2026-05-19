(ns api.routes.csrf
  (:require
   [ring.util.response]
   [api.routes.authentication]))

(defn get-routes
  []
  ["/csrf"
   {:name        ::csrf
    :middleware  [(api.routes.authentication/csrf-middleware)]
    :get         {:summary "retrieve CSRF"
                  :handler (fn [_]
                             (-> (ring.util.response/response nil)
                                 (ring.util.response/status 200)
                                 (ring.util.response/header "Cache-Control" "no-store")))}}])



