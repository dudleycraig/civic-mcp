(ns ui.utilities
  (:require
   [clojure.walk]
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

(defn get-routes-by-key-value
  [routes key value]
  (let [state (atom nil)]
    (clojure.walk/prewalk
     (fn [node] (when (and (vector? node) (map? (second node)) (= (key (second node)) value)) (reset! state (drop 2 node))) node)
     routes)
    @state))



