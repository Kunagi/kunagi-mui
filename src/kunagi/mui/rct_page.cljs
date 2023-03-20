(ns kunagi.mui.rct-page
  (:require
   ["@mui/material" :as mui]

   [kunagi.utils.rct :as rct :refer [rct]]

   [kunagi.mui.api :as ui :refer [def-ui $ <>]]
   [kunagi.mui.pages :refer [def-page]]

   [kunagi.mui.rct :as rct-ui]
   ))

(def-ui Toc [rcts]
  (ui/stack
   (for [t rcts]
     (ui/div
      {:key (-> t :id)}
      (-> t :id)
      #_(ui/DEBUG t))))
  )

(rct dummy-1
     (let [_ "dummy 1"
           _ "he"]))

(def-ui Rct [rct]
  (let [evaled-rct (ui/use-promise [(-> rct :tsm-def)] (rct/eval-rct> rct))]
    (ui/stack
     (-> rct :id)
     ($ mui/Divider)
     ($ rct-ui/Rct {:rct evaled-rct}))))


(rct dummy-2
     (let [_ "dummy 2"]))

(def-ui PageContent []
  (let [rcts-map (ui/use-atom rct/RCTS)
        rcts (->> rcts-map vals (sort-by :tsm-def-changed)
                  reverse)]
    (ui/stack
     (ui/div
      {:display :grid
       :grid-template-columns "max-content auto"
       :grid-gap 16

       :font-size "12px"}
      (ui/div
       ($ Toc {:rcts rcts}))
      (ui/div
       (when-let [rct (first rcts)]
         ($ Rct {:rct rct})))))))

(def-page page
  {:path "/ui/rct"
   :max-width false
   :content PageContent
   :title "Rich Comment Tests"})