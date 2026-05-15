(ns common.schemas.ward)

(def schema
  #?(:clj
     [{:db/ident               :ward/id
       :db/valueType           :db.type/long
       :db/cardinality         :db.cardinality/one
       :db/unique              :db.unique/identity
       :db/doc                 "The ID from GeoJSON properties (e.g., 1.0)"}

      {:db/ident               :ward/code
       :db/valueType           :db.type/long
       :db/cardinality         :db.cardinality/one
       :db/doc                 "The WD_CODE (e.g., 98399000)"}

      {:db/ident               :ward/number
       :db/valueType           :db.type/string
       :db/cardinality         :db.cardinality/one
       :db/doc                 "The WD_NO (e.g., '0')"}

      {:db/ident               :ward/gav-primary
       :db/valueType           :db.type/long
       :db/cardinality         :db.cardinality/one
       :db/doc                 "The GAVPrimary identifier from GeoJSON"}

      {:db/ident               :ward/min-x
       :db/valueType           :db.type/double
       :db/cardinality         :db.cardinality/one}

      {:db/ident               :ward/min-y
       :db/valueType           :db.type/double
       :db/cardinality         :db.cardinality/one}

      {:db/ident               :ward/max-x
       :db/valueType           :db.type/double
       :db/cardinality         :db.cardinality/one}

      {:db/ident               :ward/max-y
       :db/valueType           :db.type/double
       :db/cardinality         :db.cardinality/one}

      {:db/ident               :ward/geometry-type
       :db/valueType           :db.type/keyword
       :db/cardinality         :db.cardinality/one
       :db/doc                 "Usually :Polygon"}

      {:db/ident               :ward/coordinates-json
       :db/valueType           :db.type/string
       :db/cardinality         :db.cardinality/one
       :db/doc                 "The raw coordinates array stored as a JSON string or EDN for precise geometric verification."}

      {:db/ident               :ward/simplified
       :db/valueType           :db.type/boolean
       :db/cardinality         :db.cardinality/one
       :db/doc                 "Flag indicating if the geometry was simplified to fit storage limits."}]

     :cljs
     {:ward/id                {:db/unique :db.unique/identity}}))



