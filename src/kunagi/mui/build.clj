(ns kunagi.mui.build
  (:require
   [kunagi.build.api :as build :refer [print-task]]))


(defn update-package-json! []
  (print-task "package.json")
  (build/package-json-add-dependency! "react" "^17.0.2")
  (build/package-json-add-dependency! "react-dom" "^17.0.2")
  (build/package-json-add-dependency! "react-refresh" "^0.14.0")

  ;; (build/package-json-add-dependency! "react-router-dom" "^6.21.3")
  (build/package-json-add-dependency! "react-router-dom" "^5.2.0")

  (build/package-json-add-dependency! "@mui/material" "^5.15.7")
  (build/package-json-add-dependency! "@mui/styles" "^5.15.7")
  (build/package-json-add-dependency! "@mui/icons-material" "^5.15.7")
  (build/package-json-add-dependency! "@emotion/react" "^11.11.3")
  (build/package-json-add-dependency! "@emotion/styled" "^11.11.0")
  (build/package-json-add-dependency! "@fontsource/roboto" "^5.0.8")

  )
