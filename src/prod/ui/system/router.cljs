(ns ui.system.router
  (:require
   [integrant.core]
   [cljs.pprint]
   [datascript.core]
   [reagent.core]
   [reitit.core]
   [reitit.frontend]
   [reitit.coercion.spec]
   [reitit.frontend.easy]
   [reitit.frontend.controllers]
   [ui.routes.pages]))

(defn route-handler
  [state proposed-state]
  (let [current-state @state
        current-controllers (get current-state :route/controllers [])
        applied-controllers (reitit.frontend.controllers/apply-controllers current-controllers proposed-state)
        state-registry (reduce ;; build registry of controllers' state
                         (fn [accumulator controller]
                           (if (:name controller)
                             (assoc accumulator (:name controller) (:state controller))
                             accumulator))
                         {}
                         applied-controllers)]

    (if (= current-state @state)
      (reset! state (assoc proposed-state
                           :route/controllers applied-controllers
                           :route/state-registry state-registry))
      (.warn js/console "Navigation Superseded:"
             (get-in current-state [:data :name]) " -> "
             (get-in proposed-state [:data :name])))))

(defmethod integrant.core/init-key ::service
  [_ {configuration :configuration session :session domain :domain}]
  (let [state  (reagent.core/atom nil)
        routes (ui.routes.pages/get-routes configuration session domain)
        router (->>
                {:data {:coercion reitit.coercion.spec/coercion
                        :configuration configuration}
                 :conflicts nil}
                (reitit.frontend/router routes))]

    (reitit.frontend.easy/start!
     router
     (partial route-handler state)
     {:use-fragment false})

    {:router/routes routes :route/state state}))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  nil)
