(ns kunagi.mui.dev-preloads
(:require
 [devtools.core :as devtools]
 [helix.experimental.refresh :as helix-refresh])
  )

;; * devtools
(devtools/install! [:formatters :hints])

;; * helix
(helix-refresh/inject-hook!)
(defn ^:dev/after-load _helix-refresh []
  (helix-refresh/refresh!))
