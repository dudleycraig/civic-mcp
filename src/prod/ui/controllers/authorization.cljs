(ns ui.controllers.authorization
  (:require
   [cljs.pprint]
   [reagent.core]
   [clojure.string]
   [reitit.frontend.easy]
   [clojure.spec.alpha]
   [common.specs.user]
   [ui.api]))

(defn authenticated?
  [{session-state :session/state}]
  (and (:user/email @session-state) (:user/roles @session-state)))

(defn authorized?
  [session route]
  (let [route-roles (get-in route [:data :roles] [])
        user-roles (get @(:session/state session) :user/roles [])]
    (some (set route-roles) (map :role/name user-roles))))

(defn controller
  [configuration session _domain]
  {:identity  (fn [route-state] route-state)

   :start     (fn [route-state]
                (try
                  (if (authenticated? session)
                    (when-not (authorized? session route-state) (throw (ex-info "Unauthorized" {})))
                    (throw (ex-info "Unauthenticated" {})))
                  (catch :default error
                    (. js/console error (ex-message error))
                    (reitit.frontend.easy/push-state :ui.routes.pages/login))))

   :stop      (fn [route-state] nil)})
