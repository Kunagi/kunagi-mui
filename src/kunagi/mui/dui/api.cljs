(ns kunagi.mui.dui.api
  "Dialog User Interface"
  (:require
   [promesa.core :as p]
   [ctx :as ctx]
   [kunagi.utils :as u]
   [kunagi.mui.api :as ui :refer [def-ui $]]))

;;; impl
;; intended to be moved to own namespace

(defrecord Dialog [rows content-label])
(defrecord Row [id cells header])
(defrecord Cell [id text action])

(defn >cell [cell row dialog]
  (let [on-click (when-let [action (-> cell :action)]
                   #((-> dialog :exec-f) action))
        content (ui/div (-> cell :text))]
    (ui/div
     {:border "1px solid #555"
      :border-radius 4
      :padding "4px 8px"}

     (if on-click
       (ui/div
        {:on-click on-click
         :cursor :pointer}
        content)
       content))))

(defn >row [row dialog]
  (ui/div
   {                                    ;:border "1px solid #555"
    ;; :border-radius 4
    ;; :padding "4px 8px"
    }

   ;; header
   (when-let [header (-> row :header)]
     (ui/div
      header))

   ;; cells
   (when-let [cells (-> row :cells seq)]
     (ui/div
      {
       ;; :display :grid
       ;; :grid-template-columns (str (->> (repeat (count cells) "max-content ")
                                        ;; (apply str))
                                   ;; "auto")
       ;; :grid-gap 4

       :display :flex
       :gap 4
       }
      (for [cell cells]
        (ui/div
         {:key (-> cell :id)}
         (>cell cell row dialog)))))))

(defn >dialog [dialog]
  (ui/div
   {:border "1px solid black"
    :border-radius 4
    :background-color "#333" :color "#ddd"
    :padding 8

    :word-break :break-word}

   (if (nil? dialog)
     (ui/div
      {:text-align :center
       :color "yellow"}
      "loading...")

     (ui/stack-3

      (when-let [label (-> dialog :content-label)]
        (ui/div
         {:color "#999"}
         label))

      (ui/stack-2
       (for [row (-> dialog :rows)]
         (ui/div
          {:key (-> row :id)}
          (>row row dialog))))

      (when (ui/debug?)
        (ui/div
         (ui/div "--- DEBUG ---")
         (ui/data dialog)))))))


;;; API

;; (defn debug []
  ;; (let [ref-1 (ctx/->Ref :Person 23)
        ;; m {:customer ref-1
           ;; :comment "nice guy"}]
;; m))

;;; value display

(defn ->cell-for-value-display [id value]
  (assert id)
  (->Cell id
          (str value)
          nil))

(defn type-label [v]
  (cond
    (nil? v) "nil"
    (string? v) "string"

    (int? v) "int"
    (number? v) "number"

    (vector? v) "vector"
    (map? v) "map"
    (list? v) "list"
    (set? v) "set"

    :else (str (type v))))

(defn ->row-for-value-display [id value]
  (assert id)
  (->Row id [(->cell-for-value-display 1 value)] (type-label value)))

(defn ->dialog-for-value-display [value label]
  (->Dialog [(->row-for-value-display 1 value)]
          label))

;;; message

(defn ->row-for-message [id message]
  (->Row id
         []
         (str message)))

(defn ->dialog-for-message [message]
  (->Dialog [(->row-for-message 1 message)]
            "message"))

;;; menu

(defn ->cell-for-menu-item [item]
  (let [action (or (-> item :action)
                   (when-let [dialog (-> item :dialog)]
                     (cond
                       (instance? Dialog dialog) (fn []
                                                   dialog))))]
    (->Cell 1
            (str (or (-> item :label)
                     (-> item :text)
                     (-> item :id)))
            action)))

(defn ->row-for-menu-item [item]
  (->Row (or (-> item :id)
             (-> item :label)
             item)
         [(->cell-for-menu-item item)]
         nil))

(defn ->dialog-for-menu [items title]
  (->Dialog (->> items
                 (map ->row-for-menu-item))
            title))

;;; auto-detection

(defn ->dialog [thing]
  (cond

    (and (map? thing)
         (-> thing :rows))
    (->Dialog (-> thing :rows) {})

    :else
    (->dialog-for-value-display thing {:content-label "a value"})))

;;; action result

(defn ->dialog-for-action-result [result]
  (cond

    (instance? Dialog result)
    result

    :else
    (->dialog-for-value-display result "action result")))

;;; faciliator

(def-ui DialogFaciliator [dialog reference]
  (assert (not (and dialog reference)))
  (when dialog
    (assert (instance? Dialog dialog)))
  (let [[current-dialog set-current-dialog] (ui/use-state dialog)
        exec (fn [f]
               (let [d current-dialog]
                 (set-current-dialog (->dialog-for-message "executing action..."))
                 (p/let [result (f)]
                   (set-current-dialog (if result
                                         (->dialog-for-action-result
                                          result)
                                         d)))))]

    (ui/use-effect
     [dialog]
     (set-current-dialog dialog)
     nil)

    #_(ui/use-effect
       [reference]
       (when reference
         (p/let [thing (ctx/resolve> reference)]
           (set-dialog (->dialog thing))))
       nil)

    (ui/div
     {:class "DialogFaciliator"
      ;; :max-width "100%"
      ;; :overflow :auto
      }
     (>dialog (assoc current-dialog
                     :exec-f exec)))))

;;; state

(defonce STATE (atom {:dialogs {}}))
