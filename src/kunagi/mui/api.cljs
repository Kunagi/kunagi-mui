(ns kunagi.mui.api
  (:require-macros [kunagi.mui.api])
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
  (core/<>
   (core/$ mui/CssBaseline {:enableColorScheme true})
   (core/$ mui/ThemeProvider {:theme (-> theme clj->js mui/createTheme)}
           (core/$ router/BrowserRouter
                   children))))
