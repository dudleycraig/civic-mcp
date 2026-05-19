(ns common.entities.ward
  (:require
   [clojure.spec.alpha]
   [common.specs.ward]
   #? (:clj [clojure.data.json])
   #? (:clj [clojure.java.io])
   #? (:clj [clojure.tools.logging])
   #? (:clj [datomic.client.api])
   #? (:cljs ["jsts/org/locationtech/jts/geom" :as jsts-geom]))
  #? (:clj
      (:import
       [org.locationtech.jts.simplify DouglasPeuckerSimplifier]
       [org.locationtech.jts.geom GeometryFactory Coordinate PrecisionModel Polygon MultiPolygon LinearRing]
       [org.locationtech.proj4j CRSFactory CoordinateTransformFactory ProjCoordinate])))

(defn calculate-bbox
  [coords]
  (let [all-points  (partition 2 (flatten coords))
        lons        (map first all-points)
        lats        (map second all-points)]
    {:min-x (double (apply min lons))
     :max-x (double (apply max lons))
     :min-y (double (apply min lats))
     :max-y (double (apply max lats))}))

(defn map-coords
  [f coords]
  (cond
    (and (sequential? coords) (number? (first coords))) (f coords)
    (sequential? coords) (mapv (partial map-coords f) coords)
    :else coords))

#?(:clj
   (defn transform-coords
     [coords]
     (let [crs-factory (CRSFactory.)
           wgs84 (.createFromName crs-factory "EPSG:4326")
           web-mercator (.createFromName crs-factory "EPSG:3857")
           ct-factory (CoordinateTransformFactory.)
           transform (.createTransform ct-factory wgs84 web-mercator)

           transform-point (fn [pt]
                             (let [source (ProjCoordinate. (double (first pt)) (double (second pt)))
                                   target (ProjCoordinate.)]
                               (.transform transform source target)
                               [(.x target) (.y target)]))]
       (map-coords transform-point coords))))

(defn add!
  [transact-database-entities entities]
  (transact-database-entities
   (clojure.spec.alpha/coll-of :ward/spec :kind vector?)
   entities))

#?(:clj
   (do
     (defn jts->coords
       "Converts a JTS Geometry object back into a GeoJSON-style coordinate array."
       [geom]
       (cond
         (instance? Polygon geom)
         (let [shell (mapv (fn [c] [(.x c) (.y c)]) (.getCoordinates (.getExteriorRing geom)))
               holes (mapv (fn [idx] (mapv (fn [c] [(.x c) (.y c)]) (.getCoordinates (.getInteriorRingN geom idx))))
                           (range (.getNumInteriorRing geom)))]
           (into [shell] holes))

         (instance? MultiPolygon geom)
         (mapv (fn [idx] (jts->coords (.getGeometryN geom idx)))
               (range (.getNumGeometries geom)))))

     (defn coords->jts
       "Converts a GeoJSON coordinate array into a JTS Geometry object."
       [factory type coords]
       (let [create-ring (fn [ring-coords]
                           (.createLinearRing factory (into-array Coordinate (map (fn [[lon lat]] (Coordinate. lon lat)) ring-coords))))
             create-poly (fn [poly-coords]
                           (let [shell (create-ring (first poly-coords))
                                 holes (into-array LinearRing (map create-ring (rest poly-coords)))]
                             (.createPolygon factory shell holes)))]
         (if (= type "Polygon")
           (create-poly coords)
           (.createMultiPolygon factory (into-array Polygon (map create-poly coords))))))))

#?(:clj
   (defn feature->entity
     "Transforms a single GeoJSON 'Feature' into a Datomic transaction map."
     [feature]
     (let [props  (get feature "properties")
           geom   (get feature "geometry")
           type   (get geom "type")
           coords (transform-coords (get geom "coordinates"))
           bbox   (calculate-bbox coords)
           json   (clojure.data.json/write-str coords)]
       (if (<= (count json) 4000)
         (merge
          {:ward/id                (long (get props "ID"))
           :ward/code              (long (get props "WD_CODE"))
           :ward/number            (str (get props "WD_NO"))
           :ward/gav-primary       (long (get props "GAVPrimary"))
           :ward/geometry-type     (keyword type)
           :ward/min-x             (:min-x bbox)
           :ward/max-x             (:max-x bbox)
           :ward/min-y             (:min-y bbox)
           :ward/max-y             (:max-y bbox)
           :ward/coordinates-json  json})
         (let [factory (GeometryFactory. (PrecisionModel.) 3857)
               jts-geom (coords->jts factory type coords)
               ;; Iteratively simplify until size is under 3500 (safer margin)
               simplified-json (loop [tolerance 100.0]
                                 (let [s (DouglasPeuckerSimplifier/simplify jts-geom tolerance)
                                       sj (clojure.data.json/write-str (jts->coords s))]
                                   (if (or (<= (count sj) 3500) (> tolerance 10000.0))
                                     sj
                                     (recur (* tolerance 2.0)))))]
           (merge
            {:ward/id                (long (get props "ID"))
             :ward/code              (long (get props "WD_CODE"))
             :ward/number            (str (get props "WD_NO"))
             :ward/gav-primary       (long (get props "GAVPrimary"))
             :ward/geometry-type     (keyword type)
             :ward/min-x             (:min-x bbox)
             :ward/max-x             (:max-x bbox)
             :ward/min-y             (:min-y bbox)
             :ward/max-y             (:max-y bbox)
             :ward/coordinates-json  simplified-json
             :ward/simplified        true}))))))

#?(:clj
   (defn fetch-geojson
     [path]
     (clojure.data.json/read (clojure.java.io/reader path))))

#?(:clj
   (defn add-batch!
     "add many wards"
     [transact! entities]
     (let [batch-size 50]
       (doseq [batch (partition-all batch-size entities)]
         (try
           (transact! (clojure.spec.alpha/coll-of :ward/spec :kind vector?) (vec batch))
           (catch Exception e
             (clojure.tools.logging/error e "Failed to transact ward batch")
             (throw e)))))))

#?(:clj
   (defn entities
     []
     (let [path "data/source/WARDS.ESRI102562.geojson"]
       (if-let [resource (clojure.java.io/resource path)]
         (let [geojson (clojure.data.json/read (clojure.java.io/reader resource))
               features (get geojson "features")
               entities (mapv feature->entity features)]
           (mapv first (vals (group-by :ward/id entities))))
         (do
           (throw (Exception. "Failed loading ward geojson")))))))





