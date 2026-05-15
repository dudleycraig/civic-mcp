(ns ui.controllers.authorization
  (:require
   [cljs.pprint]
   [reagent.core]
   [clojure.string]
   [reitit.frontend.easy]
   [clojure.spec.alpha]
   [common.specs.user]
   [ui.utilities]))

(defn authorized?
  [session match]
  (let [user-roles (get ((:read session)) :user/roles [])
        route-roles (get-in match [:data :roles] [])]
    (if (empty? route-roles)
      true
      (some (set route-roles) (map :role/name user-roles)))))

(defn controller
  [session _database]
  {:name      ::controller
   :state     nil
   :identity  (fn [match] match)
   :start     (fn [match]
                (when-not (authorized? session match)
                  (. js/console warn (get-in match [:data :name]) "Unauthorized")
                  (reitit.frontend.easy/push-state :ui.routes.pages/login)))
   :stop      (fn [match] nil)})
