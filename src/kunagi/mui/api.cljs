(ns kunagi.mui.api
  (:require-macros [kunagi.mui.api :refer [$ <> def-ui]])
  (:require
   ["@mui/material" :as mui]
   ["react-router-dom" :as router]
   ["@mui/material/colors" :as colors]

   [kunagi.utils.debug :as debug]
   [kunagi.mui.core :as core]))

(def memo core/memo)
(def use-atom core/use-atom)
(def use-promise core/use-promise)
(def atom-hook core/atom-hook)
(def create-ref core/create-ref)
(def mount core/mount)
(def data core/data)
(def DEBUG core/DEBUG)
(def ReactStrictMode core/ReactStrictMode)
(def ErrorBoundary core/ErrorBoundary)

(def Stack mui/Stack)

(core/def-ui AppWrapper [theme children]
  (assert (map? theme))
  (<>
   ($ mui/CssBaseline {:enableColorScheme true})
   ($ mui/ThemeProvider {:theme (-> theme clj->js mui/createTheme)}
      ($ router/BrowserRouter
         ($ ErrorBoundary
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

;; * Colors

(defn color
  "Provides a Material Color.

  Colors: `:red` `:pink` `:purple` `:deep-purple` `:indigo` `:blue` `:light-blue`
  `:cyan` `:teal` `:green` `:light-green` `:lime` `:yellow` `:amber` `:orange`
  `deep-orange` `brown` `grey` `blue-grey`

  Values: `0` `1` `2` `3` `4` `5` `6` `7` `8` `9` `:a1` `:a2` `:a4` `:a7`"
  [color-key color-value]
  (let [color (case color-key
                :red (-> colors .-red)
                :pink (-> colors .-pink)
                :purple (-> colors .-purple)
                :deep-purple (-> colors .-deepPurple)
                :indigo (-> colors .-indigo)
                :blue (-> colors .-blue)
                :light-blue (-> colors .-lightBlue)
                :cyan (-> colors .-cyan)
                :teal (-> colors .-teal)
                :green (-> colors .-green)
                :light-green (-> colors .-lightGreen)
                :lime (-> colors .-lime)
                :yellow (-> colors .-yellow)
                :amber (-> colors .-amber)
                :orange (-> colors .-orange)
                :deep-orange  (-> colors .-deepOrange)
                :brown (-> colors .-brown)
                :grey (-> colors .-grey)
                :blue-grey (-> colors .-blueGrey))
        color-value (case color-value
                      0 50
                      1 100
                      2 200
                      3 300
                      4 400
                      5 500
                      6 600
                      7 700
                      8 800
                      9 900
                      :a1 "A100"
                      :a2 "A200"
                      :a4 "A400"
                      :a7 "A700")]
    (-> color (aget color-value))))
