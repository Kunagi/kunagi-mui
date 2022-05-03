(ns kunagi.mui.showcase.main
  (:require
   [kunagi.mui.api :as ui :refer [$]]))

;; (defnc Test [context]
;;   ($))

(defn main! []
  (js/console.info "Kunagi MUI Showcase")
  (ui/mount ($ :div "hello world")
            "app")
  )
