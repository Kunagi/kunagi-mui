(ns build
  (:refer-clojure :exclude [update])
  (:require
   [clojure.tools.build.api :as b]

   [kunagi.mui.build :as mui]

   ))

(defn update
  [{:keys []}]
  (mui/update-package-json!))
