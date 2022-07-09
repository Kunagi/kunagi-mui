(ns kunagi.mui.build
  (:require
   [kunagi.build.api :as build :refer [print-task print-done print-debug]]
   ))

(defn update-package-json! []
  (print-task "package.json")
  (build/package-json-add-dependency! "react" "^17.0.2")
  (build/package-json-add-dependency! "react-dom" "^17.0.2")
  (build/package-json-add-dependency! "react-refresh" "^0.13.0")

  ;; (build/package-json-add-dependency! "react-router-dom" "^6.3.0")
  (build/package-json-add-dependency! "react-router-dom" "^5.2.0")

  (build/package-json-add-dependency! "@mui/material" "^5.8.7")
  (build/package-json-add-dependency! "@mui/styles" "^5.8.7")
  (build/package-json-add-dependency! "@mui/icons-material" "^5.8.4")
  (build/package-json-add-dependency! "@emotion/react" "^11.9.3")
  (build/package-json-add-dependency! "@emotion/styled" "^11.9.3")
  (build/package-json-add-dependency! "@fontsource/roboto" "^4.5.7")

  )
