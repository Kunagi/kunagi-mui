(ns kunagi.mui.build
  (:require
   [kunagi.build.api :as build :refer [print-task print-done print-debug]]
   ))

(defn update-package-json! []
  (print-task "package.json")
  (build/package-json-add-dependency! "react" "^17.0.2")
  (build/package-json-add-dependency! "react-dom" "^17.0.2")
  (build/package-json-add-dependency! "react-refresh" "^0.10.0")

  (build/package-json-add-dependency! "@material-ui/core" "^4.12.4")
  (build/package-json-add-dependency! "@material-ui/icons" "^4.11.3")
  (build/package-json-add-dependency! "@material-ui/lab" "^4.0.0-alpha.61")

  )
