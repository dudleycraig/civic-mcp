(ns common.specs.utilities)

(defn not-contains?
  [k]
  (fn [m]
    (not (contains? m k))))

(defn not-contains-many?
  [& ks]
  (fn [m]
    (every? #((not-contains? %) m) ks)))




