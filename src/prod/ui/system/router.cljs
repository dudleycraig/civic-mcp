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
  [state proposed-match]
  (if proposed-match
    (let [current-match @state
          current-controllers (get current-match :route/controllers [])
          applied-controllers (reitit.frontend.controllers/apply-controllers current-controllers proposed-match)
          controller-registry (reduce
                               (fn [accumulator controller]
                                 (if (:name controller)
                                   (assoc accumulator (:name controller) (select-keys controller [:state]))
                                   accumulator))
                               {}
                               applied-controllers)]

      (if (= current-match @state)
        (reset! state (->
                       proposed-match
                       (assoc :route/controllers applied-controllers)
                       (assoc-in [:data :controllers] controller-registry)))
        (.warn js/console "Route " (get-in current-match [:data :name]) " superseded by Route " (get-in proposed-match [:data :name]))))
      (reitit.frontend.easy/replace-state :ui.routes.pages/error)))

(defmethod integrant.core/init-key ::service
  [_ {api :api session :session database :database}]
  (let [state  (reagent.core/atom nil)
        routes (ui.routes.pages/get-routes api session database)
        router (->>
                {:data
                 {:coercion reitit.coercion.spec/coercion}
                 :conflicts nil}
                (reitit.frontend/router routes))]

    (reitit.frontend.easy/start!
     router
     (partial route-handler state)
     {:use-fragment false})

    {:router/routes routes :match/state state}))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  nil)




