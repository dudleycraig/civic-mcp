(ns api.routes.csrf
  (:require
   [ring.util.response]))

(defn get-routes
  []
  ["/csrf"
   {:name        ::csrf
    :get         {:summary "retrieve CSRF for login"
                  :handler (fn [_]
                             (-> (ring.util.response/response nil)
                                 (ring.util.response/status 200)
                                 (ring.util.response/header "Cache-Control" "no-store")))}}])



