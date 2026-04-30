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

(defmethod integrant.core/init-key ::service
  [_ {state :state configuration :configuration}]
  (let [match   (reagent.core/atom nil)
        routes  (into ["/"] (ui.routes.pages/get-routes configuration state))
        router  (->>
                 {:data {:coercion reitit.coercion.spec/coercion
                         :state state
                         :configuration configuration}
                  :conflicts nil}
                 (reitit.frontend/router routes))]
    (reitit.frontend.easy/start!
     router
     (fn [proposed-match]
       (swap!
        match
        (fn [current-match]
          (let [current-controllers (get current-match :router.match/controllers [])
                new-controllers (reitit.frontend.controllers/apply-controllers current-controllers proposed-match)]
            (println "new-controllers" (clj->js new-controllers))
            (assoc proposed-match :router.match/controllers new-controllers)))))
     {:use-fragment false})
    {:routes routes :match match}))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  nil)

