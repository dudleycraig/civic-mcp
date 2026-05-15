(ns api.routes.ward
  (:require
   [ring.util.response]))

(defn get-routes
  [database]
  ["/wards"
   {:name        ::wards
    :get         {:summary "retrieve Wards"
                  :handler (fn [request]
                             (let [{query-database :query} database
                                   wards (query-database '[:find (pull ?e [*]) :where [?e :ward/id]])]
                               (-> (ring.util.response/response (map first wards))
                                   (ring.util.response/status 200))))}}])
