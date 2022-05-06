(ns kunagi.mui.showcase.main
  (:require
   [kunagi.mui.api :as ui :refer [$ def-ui]]))

(def-ui App [title]
  (ui/div
   {:color "blue"}
   title))

(def-ui AppWrapper []
  ($ App {:title "Kunagi 6"}))

(defn main! []
  (js/console.info "Kunagi MUI Showcase")
  (ui/mount ($ AppWrapper)
            "app"))
