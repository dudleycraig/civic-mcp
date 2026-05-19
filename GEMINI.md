
The PRIMARY rule for the AI, you, is that you will NEVER write to files unless EXPLICITLY asked. You will only PROPOSE code changes.
The user will make the actual code changes unless they ask you directly to write the source code.

# Overview

- The web application name is "CIVIC ZA".
- The web application purpose is to show the historical demographic and voting data visualized on a 3D map of South Africa with an AI interface.
- The web application is a work in progress, so parts of it are not written yet.
- The web application consists of a UI frontend, an API backend, an administrative CLI and an MCP LLM interface.
- The web application maintains, as closely as possible, semantic adherence to RFC 9110, RFC 7617, RFC 6265bis, RFC 7519, RFC 6750, RFC 8725 and OWASP Security.
- The web application is a monorepo, a citadel, sharing "common" code between the applications API, UI, CLI, and MCP.
- The web application is configured via `bin/env.sh`, this is the global configuration file for API, UI, CLI and MCP applications.

## API

- The API is a RESTful, stateless API and is the persistence layer.
- The API source code is hosted in `src/prod/api`.
- The API namespaces are prefixed by "api".
- The API is based off of deps.edn, Jetty, Integrant, Reitit and Datalog/Datomic.
- The API uses "Basic" Authorization headers backed up by CSRF headers for authentication.
- The API uses http-only cookies to persist JWT tokens for session persistence.
- In production, the API is using the On-Prem Datomic (com.datomic/client-pro) as a Peer server.
- In development and testing, the API is using both the On-Prem Datomic (com.datomic/client-pro), and the "fat peer" (com.datomic/local).
- Primary API configuration is via `bin/env.sh`.
- Secondary API configuration is via `resources/config.edn`.
- `resources/config.edn` is read and disseminated via `src/system/configuration.clj`.
- In development, the API REPL is initiated via the console command `source ./bin/env.sh && PROFILE=dev clj -X:prod-api:dev-api:conjure`.
- In development, the API is instantiated by evaluating within `src/dev/api/user.clj`, the source `(integrant.repl/go)` and `(provision!)`.

## UI

- The UI is the primary interface, an SPA and PWA.
- The UI source code is hosted in `src/prod/ui`.
- The UI namespaces are prefixed by "ui".
- The UI is based off of deps.edn, Shadow-cljs, Integrant, Reitit, Reagent, Datalog/Datascript and TailwindCSS.
- The UI uses DaisyUI for view components.
- The UI uses a mixture of cookies and local-storage for persistence.
- The UI uses datascript for hosting in-memory domain data.
- Primary UI configuration is via `bin/env.sh`.
- Secondary UI configuration is via `shadow-cljs.edn`.
- `shadow-cljs.edn` is read and disseminated via `src/system/configuration.clj`.
- In development, the UI is instantiated via the console command `source bin/env.sh && PROFILE=dev npm run shadow:watch`.
- In development, once instantiated, the UI is available within a browser at http://127.0.0.1:3001 (bin/env.sh pending).

## UI-diorama

- The Diorama is a dependency of the UI, hosting the 3D GIS canvas.
- The Diorama, as much as possible, is a function of the data provided by the UI.
- The Diorama source code is hosted in `src/jsx/diorama`.
- The Diorama is required within the UI namespace ui.views.pages.console.
- The Diorama is based off of React (.jsx), react-three-fiber and three.js.
- The Diorama is listed as an npm dependency within `package.json`.
- The Diorama has its own watch process via npm (`npm run diorama:watch` for development).

## CLI

- This was originally the "ADMIN" application, but it was felt that "CLI" was a more intuitive name considering its usage.
- The CLI is a stateless administrative interface.
- The CLI source code is hosted in `src/prod/cli`.
- The CLI namespaces are prefixed by "cli".
- The CLI is based off of deps.edn and Integrant.

## MCP

- The MCP is an LLM interface.
- The MCP source code is hosted in `src/prod/mcp`.
- The MCP namespaces are prefixed by "mcp".
- The MCP is based off of deps.edn and Integrant.

## COMMON

- The COMMON is source code shared between the other applications.
- The COMMON source code is hosted in `src/prod/common`.
- The COMMON namespaces are prefixed by "common".





