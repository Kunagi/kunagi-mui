(ns kunagi.mui.rct-page
  (:require
   ["@mui/material" :as mui]
   [kunagi.mui.api :as ui :refer [$ def-ui]]
   [kunagi.mui.pages :refer [def-page]]
   [kunagi.mui.rct :as rct-ui]
   [kunagi.utils.rct :as rct :refer [rct]]))

(def-ui Toc [rcts current-test-id]
  (ui/stack
   (ui/div
    {:font-weight (when (= current-test-id "ALL")
                    900)}
    ($ ui/Link
       {:href (str "/ui/rct?test=ALL")}
       "ALL"))
   (for [t rcts]
     (ui/div
      {:key (-> t :id)
       :font-weight (when (-> t :id (= current-test-id))
                      900)}
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
        rct (if test-id
              (->> rcts
                   (filter #(-> % :id (= test-id)))
                   first)
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
       ($ Toc {:rcts rcts
               :current-test-id test-id}))
      (ui/div

       (when rct
         ($ Rct {:rct rct}))

       (when (= test-id "ALL")
         (ui/stack
          (for [rct (->> rcts)]
            (ui/div
             {:key (-> rct :id)}
             ($ Rct {:rct rct}))))))))))

(def-page page-2
  {:path ["rct"]
   :max-width false
   :content PageContent
   :title "Rich Comment Tests"})
