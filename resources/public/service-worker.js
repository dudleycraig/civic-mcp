const CACHE_NAME = 'civic-za'
const ASSETS_TO_CACHE = [
  '/',
  '/index.html',
  '/css/ui.css',
  '/js/ui.js',
  '/images/favicon.ico'
];

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME)
    .then ((cache) => cache.addAll(ASSETS_TO_CACHE))
    .then (() => self.skipWaiting())
  );
});

self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames.map((cache) => {
          if (cache !== CACHE_NAME) {
            return caches.delete(cache);
          }
        })
      );
    }).then (() => self.clients.claim())
  );
});

self.addEventListener('fetch', (event) => {
  event.respondWith(
    fetch(event.request)
    .catch(() => caches.match(event.request))
  );
});

