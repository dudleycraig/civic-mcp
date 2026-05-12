(ns ui.api
  (:require
   [clojure.string]))

;; TODO: move namespace to ui.routes...
;; TODO: turn this into a service that injects a configuration.

(defn keywordize
  [data]
  (cond
    (map? data)
    (reduce-kv
     (fn [acc k v]
       (let [new-k (if (string? k)
                     (let [[ns name] (clojure.string/split k #"/" 2)]
                       (if name
                         (keyword ns name)
                         (keyword ns)))
                     k)]
         (assoc acc new-k (keywordize v))))
     {}
     data)
    (vector? data) (mapv keywordize data)
    :else data))

;; TODO: move to some sort of http utilities or ...?
(defn get-cookie
  [cookie-name]
  (let [cookies (. js/document -cookie)
        prefix (str cookie-name "=")]
    (when (clojure.string/includes? cookies prefix)
      (let [start (+ (. cookies indexOf prefix) (count prefix))
            end (let [i (. cookies indexOf ";" start)]
                  (if (= i -1) (count cookies) i))]
        (.substring cookies start end)))))

(defn get-csrf
  [{{http :http} :ui}]
  (js/fetch (str (:base-url http) "/csrf")
            (clj->js {:method "GET"
                      :credentials (if (:cors http) "include" "same-origin")})))

(defn login
  [{{http :http} :ui} credentials]
  (let [csrf-token (get-cookie "csrf-token")
        basic-header (->>
                      credentials
                      ((juxt :user/email :user/password))
                      (clojure.string/join ":")
                      (. js/window btoa)
                      (str "Basic "))]
    (js/fetch (str (:base-url http) "/authentication/login")
              (clj->js {:method "POST"
                        :headers {"Authorization" basic-header "X-CSRF-Token" csrf-token}
                        :credentials (if (:cors http) "include" "same-origin")}))))

(defn logout
  [{{http :http} :ui}]
  (js/fetch (str (:base-url http) "/authentication/logout")
            (clj->js {:method "POST"
                      :credentials (if (:cors http) "include" "same-origin")})))

(defn get-session
  [{{http :http} :ui}]
  (js/fetch (str (:base-url http) "/authentication/session/verify")
            (clj->js {:method "GET"
                      :credentials (if (:cors http) "include" "same-origin")})))






