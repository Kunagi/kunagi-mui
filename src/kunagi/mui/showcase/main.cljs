(ns kunagi.mui.showcase.main
  (:require

   [helix.experimental.refresh :as helix-refresh]
   [kunagi.mui.api :as ui :refer [$ def-ui]]))

(def-ui App [title]
  ($ :div title))

;; (defnc Test [context]
;;   ($))

(def-ui AppWrapper []
  ($ App {:title "Kunagi 6"}))

(defn main! []
  (js/console.info "Kunagi MUI Showcase")
  (ui/mount ($ AppWrapper)
            "app"))
