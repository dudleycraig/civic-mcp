(ns ui.controllers.logout
  (:require
   [reagent.core]
   [cljs.pprint]
   [clojure.string]
   [reitit.frontend.easy]))

(defn logout-handler
  [api session state _event]
  ((:clear session))
  (->
   ((:post-logout api))
   (.then (fn [response]
            (if (. response -ok)
              (do
                (swap! state #(assoc % :status :success))
                (. js/console log "Logout Successful")
                (reitit.frontend.easy/push-state :ui.routes.pages/login))
              (do
                (swap! state #(assoc % :status :error))
                (. js/console error "Logout Failed")))))))

(defn controller
  [api session]
  (let [state (reagent.core/atom {:status :inert})]
    {:name ::controller
     :state state
     :identity  (fn [match] match)
     :start     (fn [match]
                  (swap!
                   state assoc
                   :on-logout (partial logout-handler api session state)))
     :stop      (fn [match] nil)}))
