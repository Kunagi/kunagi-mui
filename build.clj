(ns build
  (:require
   [clojure.tools.build.api :as b]
   [kunagi.core.build :as kunagi-build]

   [kunagi.secrets.purpose]
   ))

(defn readme
  [{:keys []}]
  (kunagi-build/write-readme-md!)
  )
