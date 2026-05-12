(ns api.routes.resources
  (:require
   [clojure.spec.alpha]
   [reitit.ring]))

(defn get-routes
  []
  ["/*"
   {:name ::resources
    :get  {:no-doc       true
           :handler      (reitit.ring/create-resource-handler
                           {:path "/"
                            :root "public"})}}])
