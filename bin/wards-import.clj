#!/usr/bin/env bb
;; to start nREPL, `bb --nrepl-server 1667`
;; to connect the nREPL via Conjure, `:ConjureConnect 1667`

(require
 '[babashka.deps :as deps])

(deps/add-deps
 '{:deps {com.datomic/client-cloud {:mvn/version "1.0.131"}}})

(require
 '[datomic.client.api :as api])

(def db-config {:server-type :peer-server
                :access-key "dudleycraig"
                :secret "password"
                :endpoint "127.0.0.1:8998"
                :validate-hostnames false})

(try
  (let [client (api/client db-config)
        conn (api/connect client {:db-name "civic-pulse"})]
    (println "Success! Connected to:" (:db-name (api/db conn)))
    (println "Database T:" (:t (api/db conn)))) ;; Quick test: Get the current database value
  (catch Exception e
    (println "Connection failed:" (.getMessage e))))


