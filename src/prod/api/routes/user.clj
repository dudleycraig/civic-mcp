(ns api.routes.user
  (:require
   [clojure.spec.alpha]
   [ring.util.response]
   [common.entities.user]))

(defn add-handler
  "adds a user"
  [{{transact-worker! :transact} :workers}]
  (fn [request]
    (let [now (java.time.Instant/now)
          transient-user (get-in request [:parameters :body])
          [private-user] (common.entities.user/add! transact-worker! transient-user)
          public-user    (common.entities.user/private->public private-user)]
      (-> (ring.util.response/response
           {:user public-user
            :messaging/messages {::add-handler
                                 {:message/status :success
                                  :message/text "Registration Successful"
                                  :message/timestamp (.toString now)}}})
          (ring.util.response/status 201)))))

(defn get-routes
  [database]
  ["/user"
   ["/add"
    {:name  ::add
     :post  {:summary      "adds a user"
             :parameters   {:body :user/transient}
             :handler      (add-handler database)}}]])




