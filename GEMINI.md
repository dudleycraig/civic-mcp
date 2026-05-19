
The PRIMARY rule for the AI, you, is that you will NEVER write to files unless EXPLICITLY asked. You will only PROPOSE code changes.
The user will make the actual code changes unless they ask you directly to write the source code.

# overview

## The Processing Pipeline (QGIS + Blender)

- Instead of setting up a dynamic tiling server, we will use GIS tools (QGIS and Blender) to pre-bake static terrain chunks tailored specifically for rapid loading into a 3D canvas.
- Import Sentinel/Landsat imagery and Copernicus DEM using QGIS.
- Use QGIS to reproject everything to EPSG:3857 (Web Mercator).
- Crop the datasets precisely to the South African border.
- Exporting Texture & Heightmaps:
    Export satellite imagery as a high-resolution .png or .jpg map texture.
    Export elevation data as a 16-bit grayscale .png displacement map (or a normalized Exr).
    Grayscale 16-bit gives 65,536 levels of height resolution, which Java and WebGL read natively without needing specialized Mapbox-RGB encoding.
    Blender (Optional Optimization): Import the QGIS textures into Blender to generate a highly optimized, decimated 3D mesh (.gltf/.glb) of South Africa's terrain.
    Loading a pre-made 3D GLTF file directly into three.js is significantly faster than building and displacing geometry at runtime in the browser.

## Backend Architecture (Clojure + Java Ecosystem)

- Keep the backend free of heavy third-party external spatial databases by relying on enterprise-grade Java GIS libraries that interop natively with Clojure.
- Spatial Indexing (JTS - Java Topology Suite): This is the industry-standard Java library for computational geometry.
- Implementation: On application startup, pull the raw GeoJSON/WKT boundary strings out of Datomic.
- Use the JTS WKTReader or a GeoJSON parser to convert them into com.vividsolutions.jts.geom.Polygon objects.
- The Index: Load these polygons into a JTS STRtree (an In-Memory R-Tree index) wrapped in a standard Clojure atom.
- The Spatial Query: When the frontend submits a coordinate, the Clojure handler converts it into a JTS Point and performs a spatial lookup against the STRtree.

```clj
(import '[com.vividsolutions.jts.geom GeometryFactory Coordinate]
        '[com.vividsolutions.jts.index.strtree STRtree])

(let [gf (GeometryFactory.)
      point (.createPoint gf (Coordinate. lng lat))
      ;; Querying the in-memory tree returns candidates instantly
      candidates (.query @spatial-tree-atom (.getEnvelopeInternal point))]
  ;; Perform strict "contains" check on the remaining short-listed candidates
  (first (filter #(.contains % point) candidates)))
```

- Datomic Integration: The objects inside the STRtree should store a payload map containing the Datomic Entity ID.
- Once the matching polygon is found, instantly query Datomic using that ID to pull rich, time-traveled relational metadata.

## Frontend Architecture (ClojureScript to JSX Bridge)

- To make the 3D canvas a pure "function of the data", application state will be managed in ClojureScript using an atom, and pass that state cleanly down into the React JSX canvas via props.

[ClojureScript State Atom] ──> [JSX Wrapper Component (Diorama)] ──> [Props as Data] ──> [React Three Fiber Canvas]

- Use Reagent to interop with React JSX elements.
- Canvas will receive assets, coordinates, layer visibility, ... (to be determined) etc as pure data.
```cljs
(ns ui.views.pages.console
  (:require
    [reagent.core]
    ["civic-za-diorama" :default Diorama]))

(defn view
  [{{{{{console-state :state} :ui.controllers.console/controller} :controllers} :data} :match}]
  (reagent.core/with-let [data (reagent.core/track #(clj->js @console-state))]
    [:div.w-full.h-screen
     [:> Diorama
      {:data        @data
       :resources   {:terrain "/images/sa-terrain-16bit" :satellite "/images/sa-satellite.png"}
       :options #js {:theme "dark" :wireframe false}}]]))
```
- The JSX file remains a clean, declarative layout built using @react-three/fiber and @react-three/drei.
- It reads the props sent from ClojureScript and maps them into 3D objects.jsx
```jsx
import React, { Suspense } from 'react';
import { Canvas } from '@react-three/fiber';
import { OrbitControls, Sky, ContactShadows, useTexture } from '@react-three/drei';
import { Scene } from './Scene';
import { World } from './World';

// Web Mercator projection utility (Spherical Mercator formula)
function latLngToMercator(lat, lng) {
  const R = 6378137; // Earth's radius in meters
  const x = lng * Math.PI / 180 * R;
  const y = Math.log(Math.tan((90 + lat) * Math.PI / 360)) * R;
  return [x, y];
}

function Terrain({ satelliteUrl, terrainUrl }) {
  // Load the static textures prepped via QGIS/Blender
  const [satMap, displacementMap] = useTexture([satelliteUrl, terrainUrl]);
  
  return (
    <mesh rotation={[-Math.PI / 2, 0, 0]}>
      {/* High segments allow the 16bit grayscale map to sharply displace vertices */}
      <planeGeometry args={[1000, 1000, 256, 256]} />
      <meshStandardMaterial 
        map={satMap} 
        displacementMap={displacementMap} 
        displacementScale={50} // Adjust based on your QGIS scale
      />
    </mesh>
  );
}

export default function ThreeCanvas({ markers, terrainUrl, satelliteUrl, onMarkerClick }) {
  return (
    <Canvas camera={{ position: [0, 500, 500], fov: 60 }}>
      <ambientLight intensity={0.5} />
      <directionalLight position={[10, 20, 10]} intensity={1} />
      
      <Terrain satelliteUrl={satelliteUrl} terrainUrl={terrainUrl} />
      
      {markers.map((marker) => {
        const [x, y] = latLngToMercator(marker.lat, marker.lng);
        return (
          <mesh 
            key={marker.id} 
            position={[x * 0.0001, 20, y * 0.0001]} // Scaled to fit three.js scene units
            onClick={() => onMarkerClick(marker.id)}
          >
            <sphereGeometry args={[2, 16, 16]} />
            <meshBasicMaterial color="red" />
          </mesh>
        );
      })}
      
      <OrbitControls makeDefault />
    </Canvas>
  );
}
```

- The Clean Data Loop
    User interacts with UI: The user adds a marker at a chosen Lat/Lng.
    ClojureScript updates the app-state atom.
    Canvas responds to State: The JSX bridge notices the prop change and instantly renders the new <mesh> marker at the calculated Web Mercator coordinate.
    Backend validation: The frontend pushes the [Lng, Lat] to your Clojure backend via an API endpoint.
    Java Interop Execution: The Clojure backend uses the JTS STRtree index to quickly isolate the target ward polygon, gets the Datomic Entity ID associated with it, and runs a Datomic query to grab the ward details.
    State resolution: The backend responds to ClojureScript with the ward metadata, which merges back into the app-state atom to dynamically update the React UI sidebar.

# Architecture

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










