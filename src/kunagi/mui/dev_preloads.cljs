(ns kunagi.mui.dev-preloads
(:require
   [helix.experimental.refresh :as helix-refresh]
   [devtools.core :as devtools])
  )

;; * devtools
(devtools/install! [:formatters :hints])

;; * helix
(helix-refresh/inject-hook!)
(defn ^:dev/after-load _helix-refresh []
  (helix-refresh/refresh!))
