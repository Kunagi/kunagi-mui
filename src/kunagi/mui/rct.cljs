(ns kunagi.mui.rct
  (:require
   ["@mui/material" :as mui]
   [promesa.core :as p]
   [kunagi.utils.rct :as rct]
   [kunagi.utils :as u]
   [kunagi.mui.api :as ui :refer [$ def-ui]]))

(def-ui Data [value]
  (ui/div
   {:font-family :monospace
    :font-style :normal
    :white-space :pre-wrap}
   (-> value u/->edn))
  )

(def-ui RctElement [element]
  (ui/div

   ;; Binding and Expression
   (ui/div
    {:display :grid
     :grid-template-columns "max-content auto"}
    (ui/div
     {:background-color (ui/color :blue 8)
      :color "white"
      :padding "4px 8px"}
     (-> element :bind-name))
    (ui/div
     {:background-color (ui/color :blue-grey 9)
      :color "white"
      :padding "4px 8px"
      :font-family :monospace}
     (-> element :expression u/->edn)))

   ;; Value
   (when-let [value (-> element :value)]
     (ui/div
      {:background-color (ui/color :green 9)
       :color "white"
       :padding "4px 8px"}
      ($ Data {:value value})))

   ;; Eval Error
   (when-let [value (-> element :eval-error)]
     (ui/div
      {:background-color (ui/color :red 9)
       :color "white"
       :padding "4px 8px"
       :font-family :monospace}
      (-> value u/->edn)))

   ;;
   ))

(def-ui Rct [rct]
  (ui/stack
   (for [element (->> rct :result :elements)]
     ($ RctElement {:key (-> element :id)
                    :element element}))
   (ui/DEBUG rct)))


(def-ui Debug []
  (let [rct-1 (ui/use-promise (rct/eval-rct> {:f (fn []
                                                   (p/let [result {:elements [{:id 1
                                                                               :bind-name "vorname"
                                                                               :expression `(str "Wi" "tek")
                                                                               :value "Witek"}
                                                                              {:id 2
                                                                               :bind-name "eval-error"
                                                                               :expression `(throw (js/Error. "boom"))
                                                                               :eval-error (js/Error. "Boom")}]}
                                                           ]
                                                     result)
                                                   )}))
        rct-2 (ui/use-promise (rct/eval-rct> (-> @rct/RCTS vals first)))]
    (ui/stack-3
     ($ Rct {:rct rct-2})
     ($ mui/Divider)
     ($ Rct {:rct rct-1})
     ($ mui/Divider)
     ($ Rct
        {:rct {:id :one
               :result {:elements [{:id 1
                                    :bind-name "vorname"
                                    :expression `(str "Wi" "tek")
                                    :value "Witek"}
                                   {:id 2
                                    :bind-name "eval-error"
                                    :expression `(throw (js/Error. "boom"))
                                    :eval-error (js/Error. "Boom")}]}}}))))
