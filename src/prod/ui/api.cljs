(ns ui.api
  (:require
    [clojure.string]))

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

(defn post-credentials
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
                        :credentials "include"}))))

