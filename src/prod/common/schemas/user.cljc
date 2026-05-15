(ns common.schemas.user)

(def schema
  #?(:clj
     [{:db/ident       :user/uuid
       :db/valueType   :db.type/uuid
       :db/cardinality :db.cardinality/one
       :db/unique      :db.unique/identity
       :db/doc         "The public identifier of the user."}

      {:db/ident       :user/email
       :db/valueType   :db.type/string
       :db/cardinality :db.cardinality/one
       :db/unique      :db.unique/identity
       :db/doc         "The email address of the user."}

      {:db/ident       :user/hash
       :db/valueType   :db.type/string
       :db/cardinality :db.cardinality/one
       :db/doc         "The hashed password of the user."}

      {:db/ident       :user/first-name
       :db/valueType   :db.type/string
       :db/cardinality :db.cardinality/one
       :db/doc         "The first name of the user."}

      {:db/ident       :user/last-name
       :db/valueType   :db.type/string
       :db/cardinality :db.cardinality/one
       :db/doc         "The last name of the user."}

      {:db/ident       :user/roles
       :db/valueType   :db.type/ref
       :db/cardinality :db.cardinality/many
       :db/doc         "The roles assigned to the user."}]

     :cljs
     {:user/uuid      {:db/unique :db.unique/identity}
      :user/email     {:db/unique :db.unique/identity}
      :user/roles     {:db/valueType :db.type/ref
                       :db/cardinality :db.cardinality/many}}))




