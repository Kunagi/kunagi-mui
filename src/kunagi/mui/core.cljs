(ns kunagi.mui.core
  (:require-macros [kunagi.mui.core :refer [$ <>
                                            defcomponent
                                            def-ui
                                            use-state
                                            use-effect]])
  (:require
   ["react" :as react]
   ["react-dom" :as rdom]
   [helix.core :as helix]
   [helix.hooks :as helix-hooks]

   [kunagi.utils :as u]
   [kunagi.utils.debug :as debug]))

(def create-ref react/createRef)
(def memo helix/memo)

(defn use-atom
  ([ATOM]
   (use-atom ATOM identity))
  ([ATOM transformator]
   (let [[value set-value] (use-state @ATOM)
         watch-key (random-uuid)]

     (use-effect
      :once
      (let [ACTIVE (volatile! true)]
        (set-value @ATOM)
        (add-watch ATOM watch-key
                   (fn [_k _r ov nv]
                     (when @ACTIVE
                       (when-not (= ov nv)
                         (set-value nv)))))
        (fn []
          (vreset! ACTIVE false)
          (remove-watch ATOM watch-key))))

     (transformator value))))

(defn atom-hook_
  ([ATOM]
   (atom-hook_ ATOM identity))
  ([ATOM transformator]
   (partial use-atom ATOM transformator)))

(def atom-hook atom-hook_ #_(memoize atom-hook_))

(defn mount [component element-id]
  (assert (string? element-id))
  (assert component)
  (rdom/render component
               (js/document.getElementById element-id)))

;; * data rendering

(defn data
  [v]
  ($ :div
     {:style {:white-space      "pre-wrap"
              :word-break "break-all"
              :font-family      "monospace"
              :font-size        "12px"
              :font-weight      400
              :font-style       "normal"
              :overflow         "auto"
              :width            "100%"
              :padding          "1rem"
              :border "1px solid white"
              :border-radius    "4px"

              :background-color "#424242"
              :color "#f5f5f5"}}
     (u/->edn v)))

;; * debug

(defn DEBUG [& datas]
  (when (debug/active?)
    (apply data datas)))

;; * errors

(defn- error-info-field [s v]
  (when v
    (<>
     ($ :div
        {:style {:color "#999"}}
        s)
     ($ :div
        ;; {:style {:color "#999"}}
        v))))

(def-ui ErrorInfo [error]
  (let [err (u/error->data error)]
    ($ :div
       {:style {:display "grid"
                :grid-template-columns "max-content auto"
                :grid-gap "4px"}}

       (error-info-field
        "msg"
        ($ :div
           {:style {:font-weight 900}}
           (-> err :message)))

       (error-info-field "type" (str (-> err :type)))

       (when-let [dat (-> err :data)]
         (error-info-field "data" (data dat)))

       (when-let [stack (-> err :stacktrace)]
         (error-info-field
          "stack"
          ($ :div
             {:style {:white-space      "pre-wrap"
                      :word-break "break-all"
                      :font-family "monospace"
                      :font-size "12px"
                      :font-weight 400
                      :font-style "normal"
                      :overflow "auto"}}
             (str stack))))

       (when-let [cause (-> err :cause)]
         (error-info-field
          "cause"
          ($ :div
             {:style {:border-left "1px solid #eee"
                      :padding-left "4px"}}
             ($ ErrorInfo {:error cause}))))

       #_(error-info-field "DATA" (data error)))))

(def-ui CollapsedError [error]
  (let [err (u/error->data error)
        [expanded set-expanded] (use-state false)]
    ($ :div
       {:style {:cursor (when-not expanded :pointer)
                :display "grid"
                :grid-gap "8px"
                :background-color "white"
                :color "black"
                :padding "8px"
                :border "2px solid darkred"
                :border-radius "8px"}
        :onClick (when-not expanded
                   #(set-expanded true))}
       ($ :div
          {:style {:text-align "center"}}
          ($ :span
             {:style {:color "white"
                      :background-color "darkred"
                      :padding "2px 4px"
                      :border-radius "4px"}}
             "Error")
          " "
          ($ :span
             {:style {:font-weight 900}}
             (u/error-user-message err)))
       ($ :div
          (if expanded
            ($ ErrorInfo {:error error})
            ($ :a
               {:style {:text-decoration "underline"
                        :text-align "center"
                        :display "block"}}
               "Details..."))))))

(defcomponent ErrorBoundary
  (constructor [this]
               (set! (.-state this) #js {:error nil}))

  ^:static
  (getDerivedStateFromError [this error]
                            #js {:error error})

  (render [this]
          (if-not (.. this -state -error)
            (.. this -props -children)
            ($ CollapsedError
               {:error (.. this -state -error)}))))
#_(def-ui ErrorBoundary [children]
  children)
