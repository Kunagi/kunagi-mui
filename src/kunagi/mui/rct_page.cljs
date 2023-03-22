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
      ($ ui/Link
         {:href (str "/ui/rct?test=" (js/encodeURIComponent (-> t :id)))}
         (-> t :id))
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
     ($ rct-ui/Rct {:rct (or evaled-rct
                             (assoc rct :evaluating? true))}))))


(rct dummy-2
     (let [_ "dummy 2"]))

(def-ui PageContent []
  (let [rcts-map (ui/use-atom rct/RCTS)
        rcts (->> rcts-map vals (sort-by :tsm-def-changed)
                  reverse)

        test-id (ui/use-url-param :test)
        rct (or (when test-id
                  (->> rcts
                       (filter #(-> % :id (= test-id)))
                       first))
                (first rcts))
        ]
    (ui/stack
     ;; (ui/DEBUG test-id)
     (ui/div
      {:display :grid
       :grid-template-columns "max-content auto"
       :grid-gap 16

       :font-size "12px"}
      (ui/div
       ($ Toc {:rcts rcts}))
      (ui/div
       (when rct
         ($ Rct {:rct rct})))))))

(def-page page
  {:path ["rct"]
   :max-width false
   :content PageContent
   :title "Rich Comment Tests"})
