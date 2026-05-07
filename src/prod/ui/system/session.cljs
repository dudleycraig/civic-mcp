(ns ui.system.session
  (:require
   [cljs.pprint]
   [reagent.core]
   [integrant.core]
   [ui.api]
   [cljs.reader]))

(def storage-key "session")

(defn update-session
  ;; TODO: check if this creates a race condition? ie will local storage be written before authorization controller is triggered?
  [configuration state]
  (-> (ui.api/get-session configuration)
      (.then  (fn [response]
                (if (. response -ok)
                  (. response json)
                  (throw (js/Error. "Session Expired")))))
      (.then  (fn [json]
                (let [session (-> json js->clj ui.api/keywordize)]
                  (.setItem js/localStorage storage-key (pr-str session))
                  (reset! state session))))
      (.catch (fn [_]
                ;; TODO: redirect to login if session-token invalid
                (.removeItem js/localStorage storage-key)
                (reset! state nil)))))

(defmethod integrant.core/init-key ::service
  [_ {configuration :configuration}]
  (let [state (reagent.core/atom (some-> (.getItem js/localStorage storage-key) cljs.reader/read-string))]
    ;; (update-session configuration state)

    {:session/state   state
     :session/load    (fn []
                        (some->>
                         (.getItem js/localStorage storage-key)
                         cljs.reader/read-string
                         (reset! state)))
     :session/clear   (fn []
                        (.removeItem js/localStorage storage-key)
                        (reset! state nil))
     :session/save    (fn [session]
                        (.setItem js/localStorage storage-key (pr-str session))
                        (reset! state session))
     :session/verify  (fn []
                        (-> (ui.api/get-session configuration)
                            (.then  (fn [response]
                                      (if (. response -ok)
                                        (. response json)
                                        (throw (js/Error. "Session Expired")))))
                            (.then  (fn [json]
                                      (let [session (-> json js->clj ui.api/keywordize)]
                                        (.setItem js/localStorage storage-key (pr-str session))
                                        (reset! state session))))
                            (.catch (fn [_]
                                      (.removeItem js/localStorage storage-key)
                                      (reset! state nil)))))}))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  nil)




