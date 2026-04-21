(ns common.entities.ward
  (:require
   [clojure.spec.alpha]
   [clojure.data.json]
   [clojure.java.io]
   #? (:clj [datomic.client.api])
   #? (:cljs ["jsts/org/locationtech/jts/geom" :as jsts-geom])
   [common.specs.ward])
  #? (:clj (:import [org.locationtech.jts.geom GeometryFactory Coordinate PrecisionModel])))

(defn calculate-bbox
  "Calculates [min-x min-y max-x max-y] bounding box for a GeoJSON Polygon coordinate array."
  [coords]
  (let [all-points  (partition 2 (flatten coords))
        lons        (map first all-points)
        lats        (map second all-points)]
    {:min-x (double (apply min lons))
     :max-x (double (apply max lons))
     :min-y (double (apply min lats))
     :max-y (double (apply max lats))}))

(defn feature->entity
  "Transforms a single GeoJSON 'Feature' into a Datomic transaction map."
  [feature]
  (let [props  (get feature "properties")
        geom   (get feature "geometry")
        coords (get geom "coordinates")
        bbox   (calculate-bbox coords)]
    {:ward/coordinates-json  (clojure.data.json/write-str coords)
     :ward/id                (long (get props "ID"))
     :ward/code              (long (get props "WD_CODE"))
     :ward/number            (str (get props "WD_NO"))
     :ward/geometry-type     :Polygon
     :ward/min-x             (:min-x bbox)
     :ward/max-x             (:max-x bbox)
     :ward/min-y             (:min-y bbox)
     :ward/max-y             (:max-y bbox)}))

(defn fetch-geojson
  [path]
  #? (:clj (clojure.data.json/read (clojure.java.io/reader path))
      :cljs {:path path}))

(defn add!
  "add a ward or wards"
  [transact! & entities]
  (transact! (clojure.spec.alpha/coll-of :ward/schema :kind vector?) entities))

(defn add-batch!
  "add many wards"
  [transact! entities]
  (doseq [batch (partition-all 100 entities)]
    (transact! (clojure.spec.alpha/coll-of :ward/schema :kind vector?) (vec batch))))

(defn add-geojson!
  "Reads a geojson ward file from either filesystem or api endpoint and transacts wards into Datomic."
  [transact! path]
  (let [geojson  (fetch-geojson path)
        features (get geojson "features")
        entities (map feature->entity features)]
    (add-batch! transact! entities)))

(defn point-in-ward?
  "A cross-platform predicate for checking if a point is in a ward.
   lat/long: doubles
   coords-json: JSON string of the polygon coordinates."
  [lat long coords-json]
  (let [coords (clojure.data.json/read-str coords-json)]
    #?(:clj
       (let [factory      (GeometryFactory. (PrecisionModel.) 4326)
             shell-coords (->>
                           (first coords)
                           (map (fn [[lon lat]] (Coordinate. lon lat)))
                           (into-array Coordinate))
             poly         (.createPolygon factory shell-coords)
             point        (.createPoint factory (Coordinate. long lat))]
         (.contains poly point))

       :cljs
       (let [factory      (jsts-geom/GeometryFactory.)
             reader       (jsts-geom/GeoJSONReader. factory)
             geom         (.read reader (clj->js {:type "Polygon" :coordinates coords}))
             point        (.createPoint factory (jsts-geom/Coordinate. long lat))]
         (.contains geom point)))))




