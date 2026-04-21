# Project

This application, a proof of concept on behalf of iSAHA using MCP for LLM tooling, a backend Clojure API and a SPA/PWA frontend UI leveraging GIS, three.js and SVG.
I need a project that converges on all of these technologies.
The idea is utilizing South African demographic data along with the voting inclinations of specific areas/wards within South Africa.
To integrate that information with a locally hosted Ollama LLM via a custom MCP service. At a later stage we'll use a free, web based LLM service.
Note, not to be confused with the vibe coding LLM, they are separate and unrelated to each other.

The Project is being developed in increments, so not everything in this document has necessarily been implemented as yet and only serves as guidelines as to how to proceed given a specific task.
Ignore the UI and API applications for the moment and primarily focus on the MCP application.
We're also not interested in Unit or Integration tests until the application skeleton is working.

---

# Overview

The Core Concept: "Civic Pulse"
An interactive 3D dashboard that allows users to "fly" through South African wards.
As you hover or select a ward, an MCP-integrated Ollama instance provides real-time, context-aware analysis of how that ward's demographics (from Census 2022) influence its specific voting trends (from NPE 2024).

The Tech Stack Breakdown

    * The Datomic service:
        * Datomic On-Prem (Pro/Starter) as a Peer Server.

    * The ADMIN CLI, src/prod/admin:
        * an Admin command line interface that exposes the data layer as tools to the console. One tool might be get-ward-stats, which utilizes the common data layer for a specific ward's data.

    * The MCP service, src/prod/mcp:
        * an MCP server that exposes the data layer as tools to LLM. One tool might be get-ward-stats, which utilizes the common data layer for a specific ward's data.
        * Ollama Integration: Use a local MCP enabled LLM (like Llama 3) to act as a "Civic Analyst." When a user clicks a ward, the frontend sends a prompt; the LLM uses the MCP tool to "fetch" the data and then returns a natural language summary of the political/social landscape.

    * The API service, src/prod/api:
        * an API server that exposes the data layer as routes to HTTP. One route might be http://localhost:3001/get-ward-stats, which utilizes the common data layer for a specific ward's data.

    * The UI client, src/prod/ui:
        * Three.js & GIS: Render the South African map in 3D. Wards could be "extruded" based on population density or voter turnout.
        * SVG & Tailwind/DaisyUI: Use SVG overlays for precise geographic boundaries and Tailwind for a sleek, modern UI control panel.

    * The COMMON codebase, src/prod/common:
        * Demographics: Use the Stats SA Census 2022 Ward-level Estimates for age, gender, and population group dynamics.
        * Voting Data: Pull the latest 2024 National and Provincial Election (NPE) results by ward from the IEC.

## Namespaces

DO NOT include the src/prod, src/dev or src/test folder prefixes as part of the namespaces.

DO NOT add ambiguity in namespacing.

Each of the folders src/prod/admin, src/prod/mcp, src/prod/api and src/prod/ui are separate and independent applications and only share source from src/prod/common.

We're building a "Citadel" type codebase.

---

# Technical

This is a monorepo Clojure/ClojureScript based project with elements of Javascript, Java, and CSS integrated.

A distributed system, Service-Oriented Architecture with a shared codebase, a Citadel with Datomic-Pro as its base.

The Project contains Clojure ADMIN tasks, referred to as ADMIN.

The Project contains Clojure MCP service, referred to as MCP.

The Project contains Clojure API service, referred to as API.

The Project contains ClojureScript SPA/PWA, referred to as UI.

All these sections inherit from a "common" codebase.

All these sections reside under "prod" as they are all compiled into the production package.

All these sections contain a "dev" folder for development tooling.

All these sections contain a "test" folder for integration and end-to-end testing.

Project structure
```bash
├── README.md
│
├── package.json
│
├── deps.edn
├── shadow-cljs.edn
│
├── postcss.config.js
├── tailwind.config.js
│
├── tests.edn
├── jest.config.js
├── jest.setup.js
│
├── src
│   ├── prod
│   │   ├── admin
│   │   │   ├── main.clj
│   │   │   ├── routes
│   │   │   └── system
│   │   │       ├── configuration.clj
│   │   │       ├── cache.clj
│   │   │       ├── database.clj
│   │   │       ├── authentication.clj
│   │   │       ├── mcp.clj
│   │   │       ├── router.clj
│   │   │       ├── http.clj
│   │   │       └── services.clj
│   │   ├── mcp
│   │   │   ├── main.clj
│   │   │   ├── routes
│   │   │   └── system
│   │   │       ├── configuration.clj
│   │   │       ├── cache.clj
│   │   │       ├── database.clj
│   │   │       ├── authentication.clj
│   │   │       ├── mcp.clj
│   │   │       ├── router.clj
│   │   │       ├── http.clj
│   │   │       └── services.clj
│   │   ├── api
│   │   │   ├── main.clj
│   │   │   ├── routes
│   │   │   └── system
│   │   │       ├── configuration.clj
│   │   │       ├── cache.clj
│   │   │       ├── database.clj
│   │   │       ├── authentication.clj
│   │   │       ├── mcp.clj
│   │   │       ├── router.clj
│   │   │       ├── http.clj
│   │   │       └── services.clj
│   │   ├── ui
│   │   │   ├── main.cljs
│   │   │   ├── routes
│   │   │   ├── views
│   │   │   │   ├── components
│   │   │   │   └── pages
│   │   │   └── system
│   │   │       ├── configuration.clj
│   │   │       ├── cache.clj
│   │   │       ├── database.clj
│   │   │       ├── authentication.clj
│   │   │       ├── mcp.clj
│   │   │       ├── router.clj
│   │   │       ├── view.clj
│   │   │       └── services.clj
│   │   └── common
│   │       ├── specs
│   │       ├── schemas
│   │       ├── routes
│   │       ├── regex.cljc
│   │       └── styles
│   │           └── ui.css
│   ├── dev
│   │   ├── admin
│   │   ├── mcp
│   │   ├── api
│   │   └── ui
│   └── test
│       ├── admin
│       ├── mcp
│       ├── api
│       └── ui
├─ logs
│  ├── admin.log
│  ├── mcp.log
│  ├── api.log
│  └── ui.log
└── resources
    ├── logback-admin.xml
    ├── logback-mcp.xml
    ├── logback-api.xml
    └── public
        ├── images
        │   └── favicon.ico
        ├── css
        ├── js
        └── index.html
```

Clojure configuration, deps.edn
```edn
{:paths
 ["resources"]

 :deps
 {org.clojure/core.async {:mvn/version "1.6.681"}
  org.clojure/core.memoize {:mvn/version "1.1.266"}
  org.clojure/core.cache {:mvn/version "1.1.234"}
  org.clojure/data.json {:mvn/version "2.5.2"}
  aero/aero {:mvn/version "1.1.6"}
  integrant/integrant {:mvn/version "0.8.0"}
  metosin/reitit {:mvn/version "0.9.2"}}

 :aliases
 {;; --- ADMIN ---
  :prod-admin
  {:ns-default admin.main
   :extra-paths ["src/prod"]
   :extra-deps {org.clojure/tools.logging {:mvn/version "1.2.4"}
                ch.qos.logback/logback-classic {:mvn/version "1.5.6"}
                ch.qos.logback/logback-core {:mvn/version "1.5.6"}
                org.slf4j/jcl-over-slf4j {:mvn/version "2.0.14"}
                org.slf4j/jul-to-slf4j {:mvn/version "2.0.14"}
                org.slf4j/log4j-over-slf4j {:mvn/version "2.0.14"}
                org.slf4j/osgi-over-slf4j {:mvn/version "2.0.14"}
                org.slf4j/slf4j-api {:mvn/version "2.0.14"}
                com.datomic/client-pro {:mvn/version "1.0.81"}
                org.locationtech.proj4j/proj4j {:mvn/version "1.4.1"}
                org.locationtech.proj4j-epsg/proj4j {:mvn/version "1.4.1"}
                org.locationtech.jts/jts-core {:mvn/version "1.20.0"}}
   :exec-fn init
   :jvm-ops ["-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory"
             "-Dlogback.configurationFile=resources/logback-admin.xml"]}

  :dev-admin
  {:extra-paths ["src/dev/admin"]
   :extra-deps {}}

  :test-admin
  {:extra-paths ["src/test"]
   :extra-deps {}}


  ;; --- MCP ---
  :prod-mcp
  {:ns-default mcp.main
   :extra-paths ["src/prod"]
   :extra-deps {org.clojure/tools.logging {:mvn/version "1.2.4"}
                ch.qos.logback/logback-classic {:mvn/version "1.5.6"}
                ch.qos.logback/logback-core {:mvn/version "1.5.6"}
                org.slf4j/jcl-over-slf4j {:mvn/version "2.0.14"}
                org.slf4j/jul-to-slf4j {:mvn/version "2.0.14"}
                org.slf4j/log4j-over-slf4j {:mvn/version "2.0.14"}
                org.slf4j/osgi-over-slf4j {:mvn/version "2.0.14"}
                org.slf4j/slf4j-api {:mvn/version "2.0.14"}
                aero/aero {:mvn/version "1.1.6"}
                com.datomic/client-pro {:mvn/version "1.0.81"}
                org.locationtech.jts/jts-core {:mvn/version "1.20.0"}}
   :jvm-ops ["-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory"
             "-Dlogback.configurationFile=resources/logback-mcp.xml"]}

  :dev-mcp
  {:extra-paths ["src/dev/mcp"]
   :extra-deps {}}

  :test-mcp
  {:extra-paths ["src/test"]
   :extra-deps {}}


  ;; --- API ---
  :prod-api
  {:ns-default api.main
   :extra-paths ["src/prod"]
   :extra-deps {org.clojure/tools.logging {:mvn/version "1.2.4"}
                ch.qos.logback/logback-classic {:mvn/version "1.5.6"}
                ch.qos.logback/logback-core {:mvn/version "1.5.6"}
                org.slf4j/jcl-over-slf4j {:mvn/version "2.0.14"}
                org.slf4j/jul-to-slf4j {:mvn/version "2.0.14"}
                org.slf4j/log4j-over-slf4j {:mvn/version "2.0.14"}
                org.slf4j/osgi-over-slf4j {:mvn/version "2.0.14"}
                org.slf4j/slf4j-api {:mvn/version "2.0.14"}
                ring/ring-core {:mvn/version "1.9.5"}
                ring/ring-defaults {:mvn/version "0.3.3"}
                ring/ring-jetty-adapter {:mvn/version "1.9.5"}
                ring-cors/ring-cors {:mvn/version "0.1.13"}
                buddy/buddy-sign {:mvn/version "3.4.333"}
                buddy/buddy-auth {:mvn/version "3.0.1"}
                buddy/buddy-hashers {:mvn/version "2.0.167"}
                aero/aero {:mvn/version "1.1.6"}
                com.datomic/client-pro {:mvn/version "1.0.81"}
                org.locationtech.jts/jts-core {:mvn/version "1.20.0"}}
   :jvm-ops ["-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory"
             "-Dlogback.configurationFile=resources/logback-api.xml"]}

  :dev-api
  {:extra-paths ["src/dev/api"]
   :extra-deps {integrant/repl {:mvn/version "0.4.0"}}
   :main-opts ["-m" "clj-kondo.main"
               "--middleware" "[refactor-nrepl.middleware/wrap-refactor cider.nrepl/cider-middleware]"
               "--interactive"]}

  :test-api
  {:extra-paths ["src/test"]
   :extra-deps {}}


  ;; --- UI ---
  :prod-ui
  {:extra-paths ["src/prod"]
   :extra-deps {reagent/reagent {:mvn/version "2.0.0"}
                datascript/datascript {:mvn/version "1.5.4"}}}

  :dev-ui
  {:extra-paths ["src/dev/ui"]
   :extra-deps {org.slf4j/slf4j-nop {:mvn/version "1.7.36"}
                thheller/shadow-cljs {:mvn/version "3.1.1"}
                mhuebert/shadow-env {:mvn/version "0.1.6"}
                integrant/repl {:mvn/version "0.4.0"}}}

  :test-ui
  {:extra-paths ["src/test"]
   :extra-deps {day8.re-frame/test {:mvn/version "0.1.7"}}}


  ;; --- uberjar ---
  :uberjar
  {:deps {org.clojure/tools.build {:mvn/version "0.10.7"}}
   :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
   :main-opts ["-e" "(clojure.tools.build.api/compile-java)" "-m" "clojure.tools.build.api" "-fn" "uber"]}}}
 ```

Clojurescript configuration, shadow-cljs.edn
```edn
{:deps
 {:aliases
  [:conjure ;; NOTE: :conjure alias loaded from ~/.clojure/deps.edn
   :prod-ui :dev-ui :test-ui]}

 :nrepl
 {:port 7888}

 :builds
 {:ui
  {:target :browser
   :output-dir "resources/public/js"
   :asset-path "/js"
   :modules {:ui {:init-fn main/init}}
   :build-hooks [(shadow-env.core/hook)]
   :devtools {:watch-dir "resources/public" :http-root "resources/public" :http-port 3001}
   :compiler-options {:optimizations :none}
   :closure-defines {config/profile "dev" config/base-url "http://localhost:3001"}}}}
```

Javascript configuration, package.json
```json
{
  "name": "shadowcljs-reagent-reframe-tailwind",
  "description": "A generic frontend SPA template.",
  "author": "Dudley Craig",
  "version": "0.0.1",
  "private": true,
  "scripts": {
    "postcss:watch": "npx postcss ./src/prod/style/ui.css -o ./resources/public/css/ui.css --verbose -w",
    "postcss:build": "npx postcss ./src/prod/style/ui.css -o ./resources/public/css/ui.css --verbose",
    "postcss:release": "npx cross-env NODE_ENV=production postcss ./src/prod/style/ui.css -o ./resources/public/css/ui.css --verbose",
    "shadow:server": "npx shadow-cljs server",
    "shadow:watch": "npx shadow-cljs watch :ui",
    "comment": "on connecting to nREPL via either shadow:cli:repl or shadow:npm:repl, remember to switch to correct build id using cljs.user=> (shadow.cljs.devtools.api/nrepl-select :ui)",
    "shadow:npm:repl": "npx shadow-cljs clj-repl :ui",
    "shadow:cli:repl": "clj -Sdeps '{:deps {nrepl/nrepl {:mvn/version \"1.5.1\"}}}' -M -m nrepl.cmdline --connect --port 7888",
    "shadow:release": "npx shadow-cljs release ui"
  },
  "devDependencies": {
    "@tailwindcss/postcss": "^4.1.5",
    "@tailwindcss/typography": "^0.5.19",
    "@testing-library/dom": "^10.4.1",
    "@testing-library/jest-dom": "^6.6.3",
    "@testing-library/react": "^16.3.0",
    "@testing-library/user-event": "^14.6.1",
    "autoprefixer": "^10.4.20",
    "concurrently": "^8.2.2",
    "cross-env": "^7.0.3",
    "cssnano": "^7.0.6",
    "daisyui": "^5.0.43",
    "global-jsdom": "^27.0.0",
    "jest": "^30.0.3",
    "jsdom": "^27.1.0",
    "npm-run-all": "^4.1.5",
    "postcss": "^8.4.38",
    "postcss-cli": "^11.0.0",
    "postcss-nesting": "^13.0.1",
    "shadow-cljs": "^3.1.1",
    "tailwindcss": "^4.1.5"
  },
  "dependencies": {
    "@heroicons/react": "^2.1.3",
    "react": "^19.2.0",
    "react-dom": "^19.2.0",
    "proj4": "2.20.2",
    "jsts": "2.11.3"
  }
}
```
---

# ADMIN Application

There is no state to handle.

A command line interface for administrative tasks, ie ...
```bash
clj -X:prod-admin :action :init-db
```
These methods are also callable via both the API application for initiating the API and via an nREPL into the API.
Handles data seeding and migrations via datomic-manage.

---

# MCP Application

```bash
source ./bin/env.sh && PROFILE=dev clj -X:prod-mcp:dev-mcp:test-mcp:conjure
```
NOTE: once REPL has been initiated (currently ":dev" mode), the MCP service needs to be initiated via `src/dev/mcp/user.clj`, evaluating `(integrant.repl/go)` and `(provision!)`.

Primarily stateful, state is wrapped within a standard clojure/atom and may be synchronous.

The MCP service is managed via Clojure CLI tools.
The prod source code is hosted in src/prod/mcp and src/prod/common.

This will use either MCPHost or Open WebUI
Tools will be defined here like "Fetches 2024 election results for a specific ward".

Specs define validation, testing and parsing of the data.
Specs are stored in src/prod/mcp/specs and src/prod/common/specs.
Each model has its own spec definition file.

Schemas define integrity and structure of the data.
Schemas are stored in src/prod/mcp/schemas and src/prod/common/schemas.
Each schema is defined for both Datomic-Pro and Datascript to maintain data parity.
Each model has its own schema definition file.

Queries are datalog requests.
Queries are stored in src/prod/mcp/queries and src/prod/common/queries.
Each query is defined for both Datomic-Pro and Datascript to maintain data parity.
Each model has its own CRUD queries definition file.

Routes are reitit.ring/router routes. 
Routes are stored in src/prod/mcp/routes.
Each model has its own CRUD routes definition file.

---

# API Application

```bash
source ./bin/env.sh && PROFILE=dev clj -X:prod-api:dev-api:test-api:conjure
```
NOTE: once REPL has been initiated (currently ":dev" mode), the API service needs to be initiated via `src/dev/api/user.clj`, evaluating `(integrant.repl/go)` and `(provision!)`.

Primarily stateless, state is wrapped within a standard clojure/atom and is asynchronous.
Domain state that needs to be persisted is stored within a datomic database.

The API is managed via Clojure CLI tools.
The production source code is hosted in src/prod/api and src/prod/common.

The API uses com.datomic/client-pro for persistence.

Specs define validation, testing and parsing of the data.
Specs are stored in src/prod/common/specs.
Each model has its own spec definition file.

Schemas define integrity and structure of the data.
Schemas are stored in src/prod/common/schemas.
Each schema is defined for both Datomic-Pro and Datascript to maintain data parity.
Each model has its own schema definition file.

Queries are datalog requests.
Queries are stored in src/prod/common/entities.
Each query is defined for both Datomic-Pro and Datascript to maintain data parity.
Each model has its own CRUD queries definition file.

Routes are reitit.ring/router routes. 
Routes are stored in src/prod/api/routes.
Each model has its own CRUD routes definition file.

The API is primarily stateless with services handled via integrant, this may change as a deeper understanding of the MCP service is gained.
The API uses JSON for transferring data, including binary data.
Integrant is configured within src/prod/api/system/services.clj.
Each service is defined within its own file, within src/prod/api/system.

## Services

Current services being
* configuration
* cache
* database
* authentication
* mcp
* router
* http

Integrant Lifecycle Flow (using mermaid)
* LR: Left-to-Right orientation (use TD for Top-Down).
* []: Rectangle for a process.
* (): Rounded rectangle for an event.
* {}: Diamond for decision logic.
* [()]: Cylinder for a database/data store. 

graph TD
      Main[main/-main] --> Services[system.services/init]
      Services --> SysMap[Integrant System Map]

      subgraph Lifecycle [Initialization Order]
          Config[system.configuration/service]
          DB[system.database/service]
          Auth[system.authentication/service]
          MCP[system.mcp/service]
          Router[system.router/service]
          HTTP[system.http/service]
      end

      Config --> DB
      Config --> Auth
      DB -.->|ig/ref| MCP
      Auth -.->|ig/ref| Router
      MCP -.->|ig/ref| Router
      Router -.->|ig/ref| HTTP

      HTTP --> Atom[main/system atom]

Data Flow Breakdown

   1. System Map Construction:
       * system.services/create-system takes a profile and returns a map.
       * Example (from src/prod/mcp/system/services.clj):
           {:system.configuration/service {:profile :dev}
            :system.mcp/service           {:configuration (ref :system.configuration/service)}}

   2. Multimethod Dispatch (`init-key`):
       * Integrant calls integrant.core/init-key for each key.
       * The implementation (e.g., in src/prod/mcp/system/mcp.clj) receives the resolved dependencies (the actual running
         config service, not the ref).

   3. State Management:
       * The resulting initialized system is a map where keys are the service names and values are the returned state of
         the init-key functions.
       * This map is reset! into a system atom in main.clj for lifecycle management (halting/restarting).

   4. Halt Process:
       * When integrant.core/halt! is called, it traverses the dependency graph in reverse order, ensuring the HTTP server
         stops before the database connection is closed.

### Configuration Service

Located at src/prod/api/system/configuration.
Logs API events to logs/api.log.
Uses system environment variables to define environment.

### Cache Service

Located at src/prod/api/system/cache.
Caches transactional data.

### Database Service

Located at src/prod/api/system/database.
Provides data persistence, including spatial GIS data.

Uses com.datomic/client-pro.

#### Transactor

1. go to the datomic installation folder in a separate shell.
```bash
cd ~/Documents/Development/datomic/1.0.7491/
```

2. create a datomic configuration for the project.
```bash
cp config/samples/dev-transactor-template.properties civic-pulse.properties
```

3. start the transactor.
```bash
bin/transactor civic-pulse.properties
```

#### Database (Datomic On-Prem (Pro/Starter) as a Peer Server)

This is rather complected ...

In mode :prod, we use com.datomic/client-pro (datomic.client.api),
a thin remote client <-> peer server (query engine and cache) <-> transactor <-> 3rd party storage.
Which cannot create or delete databases.

In modes :dev and :test, we use both com.datomic/client-pro (datomic.client.api),
a thin remote client <-> peer server (query engine and cache) <-> transactor <-> 3rd party storage,
and com.datomic/local (datomic.client.api),
a fat peer (query engine, cache and storage).
This is done so that we can create and delete the database.

##### Environment

```bash
#!/bin/bash
# use `source env.sh` to propagate environment variables within current terminal

export PROFILE=dev

export DATOMIC_STORAGE_DIR="/resources/data/persistence"
export DATOMIC_DB_NAME="civic-pulse"
export DATOMIC_ACCESS_KEY="access-key"
export DATOMIC_SECRET="password"
export DATOMIC_STORAGE_PROTOCOL="dev"
export DATOMIC_PEER_ENDPOINT="127.0.0.1:8998"
export DATOMIC_TRANSACTOR_ENDPOINT="127.0.0.1:4334"
export DATOMIC_TRANSACTOR_URI="datomic:$DATOMIC_STORAGE_PROTOCOL://$DATOMIC_TRANSACTOR_ENDPOINT/$DATOMIC_DB_NAME"
```

##### Transactor Server

Transactor instance configuration hosted in $ROOT/$DATABASE_NAME.properties 
Transactor instance started via ...
```bash
bin/transactor $DATABASE_NAME.properties
```

enter the datomic shell.
```bash
bin/shell
```

create the database (leave the shell open for direct access).
```datomic
uri = "datomic:dev://localhost:4334/civic-pulse"
Peer.createDatabase(uri);
```

##### Peer Server

Peer instance started via ...
```bash
bin/run
    -m $SERVER_TYPE
    -h $PEER_HOST
    -p $PEER_PORT
    -a $SHARED_SECRET
    -d $DATABASE_NAME,$STORAGE_TYPE://$TRANSACTOR_HOST:$TRANSACTOR_PORT/$DATABASE_NAME
```

* It is a local intermediate gateway process.
* It provides a query engine and caching for lightweight clients.
* It handles communications with the Transactor and underlying storage.
* It uses an embedded H2 database for persistent disk storage intended for development.
* Storage is managed via the *bin/transactor*.

##### Peer Client
 
* Unlike the `Peer Library` (which requires a direct connection to storage), the Client is lightweight and can be used in non-JVM languages/microservices.
* The implementation thereof is defined below ...


#### Core Libraries
* com.datomic/client-pro

### Authentication Service

Located at src/prod/api/system/authentication.clj
Takes as parameters the config service and database service
Authentication is persisted via JWT tokens stored as secure, httpOnly cookies.
The tokens are encrypted.
The encryption keys are generated as follows ...
```bash
ROOT="~/Documents/Development/iSAHA/clj-2026-pwa"
KEYPASS="password"
STOREPASS="password"
TOKENPASS="password"
mkdir -p $ROOT/resources/.ssl
openssl genrsa -passout pass:$TOKENPASS -out $ROOT/resources/.ssl/localhost-token-private.pem 2048
openssl rsa -in $ROOT/resources/.ssl/localhost-token-private.pem -out $ROOT/resources/.ssl/localhost-token-public.pem -passin pass:$TOKENPASS -outform PEM -pubout
chmod -Rv 600 $ROOT/resources/.ssl/localhost-token-private.pem
chmod -Rv 644 $ROOT/resources/.ssl/localhost-token-public.pem
```
The encryption keys are stored in /resources/.ssl.
The keys location is stipulated within a system variable and assigned to the config service.
An authentication process is as follows ...
1. First an http GET request with credentails of email "user@domain.com" and password "password" and Basic authorization header, (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (str email ":" password)))).
```bash
curl http://localhost:3001/auth/login -u "user@domain.com:password"
```
2. The API then decodes and parses the authorization header and verifies the user.
3. JWT token is created with the claim properties of<br>
{:iat [issued at, millisecond timestamp]
 :exp [expires, millisecond timestamp]
 :iss [issuer, domain]
 :aud [audience ,domain]
 :jti [jwt id, hash]
 :sub [subscription, {:email [user's email] :roles [user's role]}]},<br>
then signed via buddy.sign.jwt using rs256 algorithm and buddy.core.keys/private-key from /resources/.ssl<br>,
and then attached as a cookie with the properties<br>
{:value [JWT token]
 :expires [HTTP UTC timestamp]
 :domain [domain]
 :path [path]
 :same-site [lax]
 :secure [boolean on http/https protocol]
 :http-only [true]},<br>
including a body response of<br>
{:user {:email [user's email] :first-name [user's first name] :last-name [user's last name] :roles [vector of user's roles]}} transformed into JSON.
4. Subsequent UI requests requiring authentication and authorization by the API must contain the {:with-credentials true} header property.
5. Upon receipt of these requests, the API will verify the token hasn't expired, the claim is valid and at least one of the user's :roles is authorized to proceed with the requested.

#### Core Libraries
* buddy/buddy-sign
* buddy/buddy-auth
* buddy/buddy-hashers

### MCP Service

Located at src/prod/api/system/mcp.clj

### Router Service

Located at src/prod/api/system/router.clj
Provides and consumes RESTful API end points.

#### Core Libraries
* metosin/reitit {:mvn/version "0.9.2"}
* reitit.ring/router
* muuntaja.core/instance

### HTTP Service

Located at src/prod/api/system/http.clj
Takes as parameters the configuration service and router service.
The HTTP service is based off of jetty, ring and reitit.
The HTTP service includes a resource handler (reitit.ring/create-resource-handler) for serving /resources/public.
The HTTP service optionally applies CORS.
The HTTP service optionally applies SSL (HTTPS).
The HTTP service optionally uses a 3rd party certificates or self signed certificates.
Self signed certificates are generated as follows ...
```bash
# NOTE: ignore JKS keystore warnings regarding proprietary SHA format when generating keys.
mkdir -p $ROOT/resources/.ssl
keytool -genkeypair -noprompt -alias self -keyalg RSA -keysize 2048 -sigalg SHA256withRSA -dname "CN=localhost" -validity 365 -keypass $KEYPASS -keystore $ROOT/resources/.ssl/localhost-https.jks -storepass $STOREPASS -storetype JKS
keytool -exportcert -noprompt -rfc -alias self -file $ROOT/resources/.ssl/localhost-https.crt -keystore $ROOT/resources/.ssl/localhost-https.jks -storepass $STOREPASS -storetype JKS
keytool -importcert -noprompt -alias localhost -file $ROOT/resources/.ssl/localhost-https.crt -keypass $KEYPASS -keystore $ROOT/resources/.ssl/localhost-https.jks -storepass $STOREPASS -storetype JKS
chmod -Rv 644 $ROOT/resources/.ssl/localhost-https.jks
chmod -Rv 644 $ROOT/resources/.ssl/localhost-https.crt
```

#### Core Libraries
* ring.adapter.jetty
* ring.middleware.cores
* ring.middleware.params
* ring.middleware.cookies
* reitit.ring
* reitit.ring.middleware.parameters
* reitit.ring.middleware.muuntaja
* reitit.ring.coercion
* reitit.ring.spec
* muuntaja.core

## Dev Mode

```bash
source ./bin/env.sh && PROFILE=dev clj -X:prod-api:dev-api:test-api:conjure
```

```bash
clj -Sdeps '{:deps {nrepl/nrepl {:mvn/version "1.5.1"}}}' -M -m nrepl.cmdline --connect --port 7888
```
then within REPL execute ...
```clojure
(shadow.cljs.devtools.api/nrepl-select :ui)
```

```bash
clj -Tmcp start :config-profile :cli-assist
```

```bash
npm run postcss:watch
```

## Test Mode

## Prod Mode

---

# UI Application

Primarily stateful, state is wrapped within a standard reagent/atom and is synchronous.
Domain state that needs to be persisted is stored within a datascript instance.

The UI is managed via shadow-cljs.
The production source code is hosted in src/prod/ui and src/prod/common.

The UI is a Progressive Web Application, specifically targetting Android, IOS and Web environments.

The UI uses datascript for application state.

Specs define validation, testing and parsing of the data.
Specs are stored in src/prod/ui/specs and src/prod/common/specs.
Each model has its own spec definition file.

Schemas define integrity and structure of the data.
Schemas are stored in src/prod/ui/schemas and src/prod/common/schemas.
Each schema is defined for both Datomic-Pro and Datascript to maintain data parity.
Each model has its own schema definition file.

Queries are datalog requests.
Queries are stored in src/prod/ui/queries and src/prod/common/queries.
Each query is defined for both Datomic-Pro and Datascript to maintain data parity.
Each model has its own CRUD queries definition file.

Routes are reitit.ring/router routes. 
Routes are stored in src/prod/ui/routes.
Each model has its own CRUD routes definition file.

Views are reagent.core components. 
Views are stored in src/prod/ui/views.
Views are broken down into components and pages, hosted in src/prod/ui/views/components and src/prod/ui/views/pages respectively.

The UI is primarily stateful with services handled via integrant, this may change as a deeper understanding of the MCP service is gained.
The UI uses JSON for transferring data, including binary data.
Integrant is configured within src/prod/ui/system/services.clj.
Each service is defined within its own file, within src/prod/ui/system.

Current services being
* configuration
* cache
* state
* router
* view

### Configuration Service

Located at src/prod/ui/system/configuration.clj

### Cache Service

Located at src/prod/ui/system/cache.clj

### State Service

Located at src/prod/ui/system/state.clj

### Router Service

Located at src/prod/ui/system/router.clj

### View Service

Located at src/prod/ui/system/view.clj

---

# Domain

Define domain data primarily as standard Clojure maps.
Use clojure.spec.alpha in the src/prod/common/specs folder.
Each model's specs defined in a separate file to define the shape and constraints of all data types defined in the Domain Data tables.
Define schemas for both a Datomic Pro database and Datascript.
The Datomic Pro/Datascript schemas should represent their foreign key relationships using :db/valueType :db.type/ref, ie DON'T use relational database foreign keys, use instead non-relational datomic reference system.
Use src/prod/common/schemas folder, with each model's schema defined in a separate file, to define the shape and constraints of all data types defined in the Domain Data tables.
Defining cross-compatible schemas for both Datascript and Datomic Pro, the key is using attribute names that follow Datomic's conventions (keywords starting with :db/ident), which Datascript also respects. Both systems accept the same map structure for defining schema attributes.

### Authentication and Authorization

- Each user can have multiple roles, these roles are defined in the below table and are to be stored in the database ...

| name           | description                                                   |
|----------------|---------------------------------------------------------------|
| :administrator | create, read, update and delete all aspects of application    |
| :guest         | can only read certain aspects of application                  |

| column name                                           | data type     | not null  | description                                                                       |
|-------------------------------------------------------|---------------|-----------|-----------------------------------------------------------------------------------|
| :role/name                                            | keyword       | true      | role identity                                                                     |
| :role/description                                     | string        | false     | description of role                                                               |
| :user/uuid                                            | string        | true      | v7 UUID                                                                           |
| :user/email                                           | string        | true      | user identity, email address of user                                              |
| :user/hash                                            | string        | true      | password hash, buddy.hashers/derive password {:alg :bcrypt+blake2b-512}           |
| :user/first-name                                      | string        | false     | first name of user                                                                |
| :user/last-name                                       | string        | false     | last name of user                                                                 |
| :user/roles                                           | :role/name    | true      | identity of :role                                                                 |

### Wards

Stats SA Ward-level data. 

### Demographics

IEC's downloadable reports in CSV/Excel format to build initial dataset.

### Election Results

IEC's downloadable reports in CSV/Excel format to build initial dataset.

---

# AI

I'll refer to you as "HAL", unless you prefer another name?

## Me

I am a web applications developer that LOVES Clojure and Clojurescript as my primary tools for web applications development.

My skills are 15+ years of software development on Linux and BSD (currently MacOS) platforms.
I use a console based development environment leveraging primarily Docker, nVIM, TMUX and ZSH, making the environment extremely portable.
I use "vibe" coding, utilizing either Gemini or a locally hosted Ollama LLM (QWEN3 Coder).
I particularly enjoy the creative aspects of web development, that being 3D modelling using Blender and three.js, GIS using QGIS, SVG and standard html/css (TailwindCSS/DaisyUI).
I also create backend RESTful/GraphQL/SSE/WebSocketted APIs using Clojure.

My timezone is South African Standard Time (SAST, +2 GMT) with my standard work hours being from 08:00 to 18:00.

---

## Rules

**PROPOSE ONLY:** Do NOT write to any files unless I EXPLICITLY request it.
Output all code proposals for ME to review and manually apply to the source files.

---

## Coding

### Fully Qualified Domain Names

I prefer to use the fully qualified domain name of included libraries throughout the code base, rarely using aliases.

For example the ns require, integrant.core usually uses `[integrant.core :as ig]`, I prefer to use `[integrant.core]`.
Where I reference integrant within the code, I'll use `integrant.core/init`.
This removes any ambiguity within the code as to which library I'm referring to without my having to check the :require within the ns declaration.
This has the advantage of clarity but at the cost of verbosity.

Occasionally I will use references `:refer` or aliases `:as` for when a method is referenced a lot and the context removes ambiguity.
An example would be `(ns system.services (:require [integrant.core :refer [ref] :rename {ref iref}]))`.
Since the context is system.services managed by integrant I can easily infer what `iref` means without having to examine the namespace declaration.

The presence of `:refer` or `:as` in any namespace declaration is **always intentional and sanctioned**.
**NEVER** refactor existing usages of `:refer` or `:as` to use Fully Qualified Domain Names.
**NEVER** flag these as inconsistencies.
Treat the existance of these aliases in the code as an explicit override to the FQDN rule for that specific file.

### Conditionals

Use `if` for single condition checks, not `cond`.
Only use `cond` for multiple condition branches.
Prefer `if-let` and `when-let` for binding and testing a value in one step.
Consider `when` for conditionals with single result and no else branch.
Consider `cond->`, and `cond->>`.
Track actual values instead of boolean flags where possible.
Use early returns with `when` rather than deeply nested conditionals.
Return `nil` for "not found" conditions rather than objects with boolean flags.

### Variable Binding

Minimize code points by avoiding unnecessary `let` bindings.
Only use `let` when a value is used multiple times or when clarity demands it.
Inline values used only once rather than binding them to variables.
Use threading macros (`->`, `->>`) to eliminate intermediate bindings.

### Clojure Naming Conventions

To maintain a clear distinction between **identity** (the container), **action** (the mutation), and **logic** (the result), the following conventions are strictly followed:

#### The Predicate (`?` suffix)

*   **Intent:** Denotes a function or symbol used to determine **logical truth**.
*   **Rationale:** In Clojure, `false` and `nil` are **falsy**, while everything else is **truthy**. A predicate communicates that the result is intended for conditional logic (e.g., `if`, `when`, `filter`).
*   **Usage:** Applied to functions that evaluate a condition, regardless of whether they return a strict `true`/`false` or a truthy/falsy value (like `nil`).
*   **Example:** `valid-user?`, `empty?`, `active-session?`

<!--
#### The Atom/Identity (`$` prefix)

*   **Intent:** Specifically identifies a **state container** (Atom, Volatile, or Reference). 
*   **Rationale:** Distinguishes the "box" (the identity) from the "value" inside the box (the state). It provides a visual "speed bump" reminding the developer to `deref` or use `swap!`.
*   **Example:** `(def $user-data (atom {}))` or `(let [$count (atom 0)] ...)`

#### The Immutable System Reference (`&` prefix)

*   **Intent:** Specifically identifies an **immutable reference to a complex system snapshot** (e.g., a Datomic Basis or a DataScript snapshot).
*   **Rationale:** Distinguishes a "heavy" system-wide value from a "simple" local map or primitive. It provides a visual marker that the variable represents a global
state captured at a specific point in time.
*   **Example:** `&database-value`, `&messaging-snapshot`
  -->

#### The Mutation/Side-Effect (`!` suffix)

*   **Intent:** Warns that the symbol **changes state** or interacts with the "outside world" (I/O).
*   **Usage in Functions:** Applied to functions that perform `swap!`, `reset!`, or perform I/O (database/files).
*   **Usage in Variables:** Denotes a **Transient** (locally mutable) collection that is currently "unlocked" for performance.
*   **Example Function:** `(defn update-state! [state-atom-] (reset! state-atom- :new-val))`
*   **Example Variable:** `(let [acc! (transient [])] ...)`

#### Method Naming

Prefer simple verb action methods within namespaces.
When referring to those methods, use the fully qualified domain name (namespace) as in (system.messaging/log! message).
This gives both the context and a description of what is being done to that context.

### Destructuring

Use destructuring in function parameters and `let` bindings when accessing multiple keys.
Example: `[{zloc ::zloc match-form ::match-form :as context}]` instead of `[{:keys [::zloc ::match-form] :as context}]` and separate `let` bindings for namespaced keys.
Example: `[{zloc :zloc match-form :match-form :as context}]` for regular keywords.
Prefer to use standard destructuring over the `:keys` shorthand destructuring.

**NEVER** refactor existing usages of `:keys` in function parameters and `let` bindings.
**NEVER** flag these as inconsistencies.
Treat the existance of these `:keys` bindings in the code as an explicit override to the Destrcuturing rule for that specific function argument or `let` binding.

### Comments

Do include comments in generated code, unless specifically asked to.

### Nesting

Minimize nesting levels by using proper control flow constructs.
Use threading macros (`->`, `->>`) for sequential operations.

### Function Design

Functions should generally do one thing.
Pure functions preferred over functions with side effects.
Return useful values that can be used by callers.
Smaller functions make edits faster and reduce the number of tokens.
Reducing tokens makes me happy.

### Clojure Naming Conventions

#### Methods using simple verb method names in conjunction with the namespace

Prefer simple verb method names when they are used with their fully qualified namespace.
The namespace provides the context, making a descriptive suffix redundant and verbose.
This leads to highly readable, self-documenting code.

Example:
- Good: `(system.messaging/push! $state context message)`
- Avoid: `(system.messaging/push-message-to-queue! $state context message)`

### Library Preferences

Prefer `clojure.string` functions over Java interop for string operations.
  - Use `str/ends-with?` instead of `.endsWith`.
  - Use `str/starts-with?` instead of `.startsWith`.
  - Use `str/includes?` instead of `.contains`.
  - Use `str/blank?` instead of checking `.isEmpty` or `.trim`.
Follow Clojure naming conventions (predicates end with `?`).
Favor built-in Clojure functions that are more expressive and idiomatic.

### REPL best pratices

Always reload namespaces with `:reload` flag: `(require '[namespace] :reload)`.
Always change into namespaces that you are working on.

### Testing Best Practices

Always reload namespaces before running tests with `:reload` flag: `(require '[namespace] :reload)`.
Test both normal execution paths and error conditions.

### Using Shell Commands

Prefer the idiomatic `clojure.java.shell/sh` for executing shell commands.
Always handle potential errors from shell command execution.
Use explicit working directory for relative paths: `(shell/sh "cmd" :dir "/path")`.
For testing builds and tasks, run `clojure -X:test` instead of running tests piecemeal.
When capturing shell output, remember it may be truncated for very large outputs.
Consider using shell commands for tasks that have mature CLI tools like diffing or git operations.

**Context Maintenance**:
  - Use `clojure_eval` with `:reload` to ensure you're working with the latest code.
  - Always switch into `(in-ns ...)` the namespace that you are working on.
  - Keep function and namespace references fully qualified when crossing namespace boundaries.

### Functional Programming

Treat computation as the evaluation of mathematical functions, while avoiding state mutations.
Functions are "first class", they are pieces of data, passed as arguments, returned from other functions, stored as variables.
Immutable by default, while we have mutable data structures, we try to primarily subscribe to immutability.
Pure Functions, while we have functions that create side-effects, in the main we aspire to funcitons that given an input, will always provide the same output.
Expressions over Statements, every piece of code returns a value. There are no "actions" that don't result in data.
A fundamental shift from OOP is the separation of data and behavior.
  * A Clojure Vector instance defines the data, but its behavior/actions are decoupled.
  * A Clojure Vector instance doesn't know how to append to itself, but clojure.core/conj does.

Referential Transparency, a function call with its resulting value can be replaced without changing the programs behavior.
Concurrency Safety, because data is immutable, multiple threads can read the dame data simultaneously without locks or race conditions.
Predictability, logic is isolated from the "side-effect of time", making it easier to test and debug in isolation.

#### Patterns

- Function Composition, build complex logic by "piping" data through a chain of small, single-purpose functions.
- Recursion & recur, replacing imperative `for`/`while` loops, Clojures' `for` and `while` are not imperative, with recursive calls to avoid mutable counter variables.
- Middleware/Wrappers, Wrap functions in other functions to add behavior.
- Persistent Data Structures, use "copy-on-write" trees that share memory between old and new versions to make immutability performant.
- Data Transformation, `map`/`filter`/`reduce`, use standard high-order functions to process collections instead of manual iteration.
    * Collections
        1. A Collections' behavior is ALWAYS eager.
        2. Examples being List, Hashmap, Vector, Set etc.
        3. It stores data and provides access to data.
    * Sequence (Interface), *NOTE: A List IS a Sequence and directly implements ISeq interface, all other collections ARE Seqable but are NOT Sequences.*
        1. A Sequence/Interface is a method by which to iterate a collection.
        2. It turns a `vector`, `hashmap`, `set`, `list` into a standard "pipe" so that functions can work on them regardless of their original shape.
    * Producer (Lazy Sequence)
        1. A producer is a function that returns a **Lazy Sequence**. It wraps a Collection or another Sequence and defines a transformation.
        2. A Producers' behavior is ALWAYS lazy. *Examples being `map`, `filter`, `for`, `remove`, `range`*
        3. It defines what should happen to data without actually doing anything as yet, it's lazy.
    * Consumer
        1. A Consumer is a function that triggers the realization of a Sequence.
        2. If a Sequence isn't present, as in the case of a `vector` and a `hashmap`, a Sequence interface is added.
        3. It "pulls" the data through the producers to get a final result.
        4. A Consumers' behavior is eager/greedy. *Examples being `into`, `reduce`, `doseq`, `count`, `transduce`, `doall`*
        5. It takes a "recipe" (Producer) and turns it back into a result.

**We're going to try to maintain strict adherence to functional programming patterns where possible**

---

