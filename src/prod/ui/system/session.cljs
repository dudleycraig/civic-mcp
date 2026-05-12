(ns ui.system.session
  (:require
   [cljs.pprint]
   [reagent.core]
   [integrant.core]
   [ui.api]
   [reitit.frontend.easy]
   [cljs.reader]))

(def storage-key "session")

(defn sync
  [state event]
  (when (= (. event -key) storage-key)
    (try
      (let [session (some->> (.getItem js/localStorage storage-key) cljs.reader/read-string)]
        (reset! state session)
        (if-let [{email :user/email roles :user/roles} session]
          (when-not (and email roles)
            (throw (ex-info "Unauthenticated" {})))
          (throw (ex-info "Unauthenticated" {}))))
      (catch :default error
        (. js/console error (ex-message error))
        (reitit.frontend.easy/push-state :ui.routes.pages/login)))))

(defn verify
  [configuration state]
  (-> (ui.api/get-session configuration)
      (.then  (fn [response]
                (if (. response -ok)
                  (. response json)
                  (let [status (. response -status)]
                    (if (= status 401)
                      (throw (ex-info "Session Expired" {:type :unauthorized}))
                      (throw (ex-info "Server Error" {:type :server :status status})))))))
      (.then  (fn [json]
                (let [session (-> json js->clj ui.api/keywordize)]
                  (.setItem js/localStorage storage-key (pr-str session))
                  (reset! state session))))
      (.catch (fn [_]
                (.removeItem js/localStorage storage-key)
                (reset! state nil)))))

(defmethod integrant.core/init-key ::service
  [_ {configuration :configuration}]
  (let [state (reagent.core/atom (some->> (.getItem js/localStorage storage-key) cljs.reader/read-string))]
    (. js/window addEventListener "storage" (partial sync state))
    (verify configuration state)
    {:state   state
     :sync    (partial sync state)
     :read    (fn [] @state)
     :save    (fn [session] (.setItem js/localStorage storage-key (pr-str session)) (reset! state session))
     :verify  (partial verify configuration state)
     :clear   (fn [] (.removeItem js/localStorage storage-key) (reset! state nil))}))

(defmethod integrant.core/halt-key! ::service
  [_ {sync :sync}]
  (. js/window removeEventListener "storage" sync))




