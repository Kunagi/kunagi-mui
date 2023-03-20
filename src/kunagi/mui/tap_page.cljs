(ns kunagi.mui.tap-page
  (:require
   ["@mui/material" :as mui]

   [kunagi.utils.rct :as rct :refer [rct]]

   [kunagi.mui.api :as ui :refer [def-ui $ <>]]
   [kunagi.mui.pages :refer [def-page]]

   [kunagi.mui.rct :as rct-ui]

   [tick.core :as tick]))

(defonce TAPS (atom nil))

(defn add-tap-item [src time payload]
  (swap! TAPS
         (fn [taps]
           (-> taps
               (conj [src time payload])
               (->>
                (take 42))))))

(defn activate! []
  (when-not @TAPS
    (reset! TAPS '())
    (add-tap #(add-tap-item :local (js/Date.) %))))

(def-ui Taps [extra-taps]
  (let [taps (ui/use-atom TAPS)
        all-taps (concat taps extra-taps)]

    (ui/use-effect
     :once
     (activate!)
     nil)

    (ui/stack

     (for [[src time payload] (->> all-taps (sort-by second) reverse)]
       (ui/div
        {:key [src time]}
        (ui/div
         (-> time tick/date-time tick/time str)
         " "
         (str src))
        (ui/data payload))))))

(def-ui PageContent []
  ($ Taps))

(activate!)

(tap> :boo)

(def-page page
  {:path "/ui/tap"
   :max-width false
   :content PageContent
   :title "Tap"})
