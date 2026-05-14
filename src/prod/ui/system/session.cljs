(ns ui.system.session
  (:require
   [clojure.string]
   [cljs.pprint]
   [reagent.core]
   [integrant.core]
   [reitit.frontend.easy]
   [cljs.reader]
   [ui.utilities]))

(def storage-key "session")

(defn sync
  [state event]
  (swap! state assoc :status :synchronizing)
  (if (= (. event -key) storage-key)
    (try
      (let [session (some->> (.getItem js/localStorage storage-key) cljs.reader/read-string)]
        (if-let [{email :user/email roles :user/roles} session]
          (if (and email roles)
            (swap! state assoc :data session :status :logged-in)
            (throw (ex-info "Unauthenticated" {})))
          (throw (ex-info "Unauthenticated" {}))))
      (catch :default error
        (. js/console error (ex-message error))
        (swap! state assoc :data nil :status :logged-out)
        (.removeItem js/localStorage storage-key)
        (reitit.frontend.easy/push-state :ui.routes.pages/login)))
    (swap! state assoc :status :logged-out)))

(defn verify
  [api state]
  (swap! state assoc :status :verifying)
  (-> ((:get-session-verify api))
      (.then  (fn [response]
                (if (. response -ok)
                  (. response json)
                  (let [response-status (. response -status)]
                    (if (= response-status 401)
                      (throw (ex-info "Session Expired" {:type :unauthorized}))
                      (throw (ex-info "Server Error" {:type :server :status response-status})))))))
      (.then  (fn [json]
                (let [session (-> json js->clj ui.utilities/keywordize)]
                  (swap! state assoc :data session :status :logged-in)
                  (.setItem js/localStorage storage-key (pr-str session)))))
      (.catch (fn [_]
                (swap! state assoc :data nil :status :logged-out)
                (.removeItem js/localStorage storage-key)))))

(defmethod integrant.core/init-key ::service
  [_ {api :api}]
  (let [{user-email :user/email user-roles :user-roles :as session-data} (some->> (.getItem js/localStorage storage-key) cljs.reader/read-string)
        session-status (if (and user-email user-roles) :logged-in :logged-out)
        state (reagent.core/atom {:status session-status :data session-data})]
    (. js/window addEventListener "storage" (partial sync state))
    (verify api state)
    {:state   state
     :sync    (partial      sync state)
     :verify  (partial      verify api state)

     ;; we don't update state in these methods as the storage event listener (sync) should handle state updates authomatically.
     :save    (fn [session] (.setItem js/localStorage storage-key (pr-str session)))
     :clear   (fn []        (.removeItem js/localStorage storage-key))
    
     ;; not sure if we should read from the main source of truth or from volatile state which is faster but less reliable? 
     :read    (fn []        (some->> (.getItem js/localStorage storage-key) cljs.reader/read-string))}))

(defmethod integrant.core/halt-key! ::service
  [_ {sync :sync}]
  (. js/window removeEventListener "storage" sync))




