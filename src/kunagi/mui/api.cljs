(ns kunagi.mui.api
  (:require-macros [kunagi.mui.api :refer [$ <> def-ui]])
  (:require
   ["@mui/material" :as mui]
   ["@mui/material/colors" :as colors]
   ["react-router-dom" :as router]
   [camel-snake-kebab.core :as csk]
   [clojure.string :as str]
   [kunagi.utils.browser :as browser]
   [kunagi.mui.core :as core]
   [kunagi.utils.debug :as debug]))

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

;; * styles


(def attr-with-px?
  #{:padding "padding"
    :padding-top "padding-top"
    :padding-bottom "padding-bottom"
    :padding-left "padding-left"
    :padding-right "padding-right"

    :margin "margin"
    :margin-top "margin-top"
    :margin-bottom "margin-bottom"
    :margin-left "margin-left"
    :margin-right "margin-right"

    :grid-gap "grid-gap"
    :gap "gap"})

(defn- conform-style-value [v attr]
  (cond
    (vector? v)                (->> v (map #(conform-style-value % attr)) (str/join " "))
    (string? v)                v
    (keyword? v)               (name v)
    (= 0 v)                    v
    (and (number? v)
         (attr-with-px? attr)) (str v "px")
    :else                      v))

(defn- ->camelCase-last [s]
  (let [strings (str/split s #"\s")
        ]
    (if (-> strings count (= 1))
      (csk/->camelCase (first strings))
      (str/join " "
                (conj (butlast strings)
                      (csk/->camelCase (last strings)))))))
(defn- conform-style-key [k camel?]
  (if camel?
    (if (keyword? k)
      (-> k name ->camelCase-last)
      (->camelCase-last k))
    (if (keyword? k)
      (-> k name)
      (str k))))

(defn- conform-style [styles camel?]
  (reduce (fn [styles [k v]]
            (if (and (string? k) (str/starts-with? "&" k))
              (assoc styles (conform-style-key k camel?) (conform-style v camel?))
              (assoc styles (conform-style-key k camel?) (conform-style-value v k))))
          {} styles))

(defn- conform-styles-selector [s]
  (cond
    (keyword? s) (str "& " (name s))
    (str/starts-with? s "& ") s
    :else (str "& " s)))

(defn conform-styles [styles camel?]
  (reduce (fn [styles [k v]]
            (if (map? v)
              (assoc styles
                     (conform-styles-selector k)
                     (conform-style v camel?))
              (assoc styles
                     (conform-style-key k camel?)
                     (conform-style-value v k))))
          {} styles))

(defn sx [css-map]
  (when css-map
    (-> css-map
        (conform-styles true)
        clj->js)))

(def ->sx sx)

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

;; url

(def use-url-params (atom-hook browser/URL_PARAMS))

(defn use-url-param [k]
  (get (use-url-params)
       (cond
         (nil? k) nil
         (keyword k) k
         (string? k) (keyword k)
         :else (str k))))

;; * links

(defn coerce-link-to [to]
  (when to
    (cond
      (string? to)     to
      (sequential? to) (str "/ui/" (->> to (str/join "/")))
      :else            (throw (ex-info "Unsupported `to` property value."
                                       {:to   to
                                        :type (type to)})))))

(defn to-is-remote? [to]
  (and (string? to)
       (or (-> to (.startsWith "https:"))
           (-> to (.startsWith "http:")))))

(defn to-is-applink? [to]
  (and (string? to)
       (or (-> to (.startsWith "mailto:"))
           (-> to (.startsWith "tel:")))))

(def RouterLink router/Link)

(def-ui Link [to href on-click class className sx children style]
  (let [to        (or to href)
        className (or class className)
        remote?   (to-is-remote? to)
        applink? (to-is-applink? to)

        on-click (if (and on-click
                          (not href)
                          (not to))
                   (fn [^js event]
                     (-> event .preventDefault)
                     ;; (when (-> event .-stopImmediatePropagation)
                     ;;   (-> event .stopImmediatePropagation))
                     ;; (-> event .stopPropagation)
                     (on-click))
                   on-click)]
    (if (or remote? applink? (nil? to))
      ($ :a
         {:href      to
          :onClick   on-click
          :target    (when remote? "_blank")
          :className className
          :sx (->sx sx)
          :style     (merge {:cursor "pointer"
                             :color  "unset"}
                            style)}
         children)
      ($ router/Link
         {:to        (coerce-link-to to)
          :onClick   on-click
          :className (str "Link " className)
          :sx (->sx sx)}
         children))))
