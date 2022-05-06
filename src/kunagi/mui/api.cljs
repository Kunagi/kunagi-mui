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

(core/def-ui AppWrapper [theme children]
  (assert theme)
  (core/<>
   (core/$ mui/CssBaseline {:enableColorScheme true})
   (core/$ mui/ThemeProvider {:theme (-> theme clj->js mui/createTheme)}
           (core/$ router/BrowserRouter
                   children))))
