(ns kunagi.mui.api
  (:require-macros [kunagi.mui.api :refer [$ <> def-ui]])
  (:require
   ["@mui/material" :as mui]
   ["react-router-dom" :as router]

   [kunagi.utils.debug :as debug]
   [kunagi.mui.core :as core]))

(def memo core/memo)
(def use-atom core/use-atom)
(def atom-hook core/atom-hook)
(def create-ref core/create-ref)
(def mount core/mount)
(def data core/data)
(def DEBUG core/DEBUG)

(def Stack mui/Stack)

(core/def-ui AppWrapper [theme children]
  (assert (map? theme))
  (<>
   ($ mui/CssBaseline {:enableColorScheme true})
   ($ mui/ThemeProvider {:theme (-> theme clj->js mui/createTheme)}
      ($ router/BrowserRouter
         ($ core/ErrorBoundary
            children)))))

(def-ui Debug [label value]
  (let [[expanded? set-expanded] (core/use-state false)]
    (when (debug/active?)
      ($ :div
         {:style {:font-size "10px"
                  :background-color "black"
                  :color "#AFA"
                  :padding "3px"
                  :cursor "pointer"
                  :border-radius "5px"}
          :onClick #(set-expanded (not expanded?))}
         ($ :div
            "[debug: " label "]")
         (when expanded?
           (data value))))))

(defn DEBUG_
  ([value]
   (DEBUG_ "?" value))
  ([label value]
   (when (debug/active?)
     ($ Debug {:label label :value value}))))
