(ns views.components.sensor-display
  (:require [reagent.core :as r]))

(def sensor-xf ;; transducer
  (comp
   (filter #(>= % 0))
   (map #(* % 1.5))
   (map (fn [v] ^{:key v} [:li (str "Reading: " v)]))))

(defn sensor-display
  "displays big data for sensor readings"
  [$sensor-data]
  (let [rf (sensor-xf conj!)]
    (fn []
      (let [data @$sensor-data
            processed-hiccup
            (loop [items   data
                   result! (transient [])]
              (if (empty? items)
                (persistent! result!)
                (recur (rest items)
                       (rf result! (first items)))))]
        [:div
         [:h2 "Sensor Readings"]
         [:ul processed-hiccup]]))))


