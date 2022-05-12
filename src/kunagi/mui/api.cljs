(ns kunagi.mui.api
  (:require-macros [kunagi.mui.api :refer [$ <>]])
  (:require
   ["@mui/material" :as mui]
   ["react-router-dom" :as router]

   [kunagi.mui.core :as core]))

(def memo core/memo)
(def atom-hook core/atom-hook)
(def create-ref core/create-ref)
(def mount core/mount)
(def data core/data)
(def DEBUG core/DEBUG)

(core/def-ui AppWrapper [theme children]
  (assert (map? theme))
  (<>
   ($ mui/CssBaseline {:enableColorScheme true})
   ($ mui/ThemeProvider {:theme (-> theme clj->js mui/createTheme)}
      ($ router/BrowserRouter
         ($ core/ErrorBoundary
            children)))))
