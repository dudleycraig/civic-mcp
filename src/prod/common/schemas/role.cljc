(ns common.schemas.role)

(def schema
  #?(:clj
     [{:db/ident           :role/name
       :db/valueType       :db.type/keyword
       :db/cardinality     :db.cardinality/one
       :db/unique          :db.unique/identity
       :db/doc             "The name of the role."}

      {:db/ident           :role/description
       :db/valueType       :db.type/string
       :db/cardinality     :db.cardinality/one
       :db/doc             "The description of the role."}]

     :cljs
     {:role/name          {:db/unique :db.unique/identity}}))




