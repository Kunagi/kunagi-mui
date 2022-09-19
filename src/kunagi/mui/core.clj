(ns kunagi.mui.core
  (:require
   [clojure.string :as str]
   [helix.core :as helix]
   [helix.hooks :as helix-hooks]))

;; https://cljdoc.org/d/lilactown/helix/0.0.13/doc/pro-tips
(defmacro defnc [type params & body]
  (let [[docstring params body] (if (string? params)
                                  [params (first body) (rest body)]
                                  [nil params body])
        opts? (map? (first body)) ;; whether an opts map was passed in
        opts (if opts?
               (first body)
               {})
        body (if opts?
               (rest body)
               body)
        ;; feature flags to enable by default
        default-opts {:helix/features {:fast-refresh true
                                       :check-invalid-hooks-usage true}}]
    `(helix.core/defnc ~type ~@(when docstring [docstring]) ~params
       ;; we use `merge` here to allow indidivual consumers to override feature
       ;; flags in special cases
       ~(merge default-opts opts)
       ~@body)))

(defmacro defcomponent [type & args] `(helix/defcomponent ~type ~@args))
(defmacro $ [type & args] `(helix/$ ~type ~@args))
(defmacro <> [& children] `(helix/<> ~@children))
(defmacro create-context [& body] `(helix/create-context ~@body))
(defmacro provider [opts & children] `(helix/provider ~opts ~@children))

(defmacro use-context [& body] `(helix-hooks/use-context ~@body))
(defmacro use-state [& body] `(helix-hooks/use-state ~@body))
(defmacro use-effect [& body] `(helix-hooks/use-effect ~@body))
(defmacro use-memo [& body] `(helix-hooks/use-memo ~@body))

(defmacro def-ui [type params & body]
  (let [[docstring params body] (if (string? params)
                                  [params (first body) (rest body)]
                                  [nil params body])

        params (if (-> params first map?)
                 params
                 [{:keys params}])

        opts? (map? (first body))
        opts  (if opts?
                (first body)
                {})
        body  (if opts?
                (rest body)
                body)

        lets []

        ;; lets (if-let [syms (get opts :from-context)]
        ;;        (let [lets (into lets [`~'context_ `(use-spark-context)])]
        ;;          (reduce (fn [lets sym]
        ;;                    (into lets
        ;;                          [`~sym `(or ~sym
        ;;                                      (get ~'context_ ~(keyword sym)))]))
        ;;                  lets syms))
        ;;        lets)

        opts (if-let [wrap-memo-props (get opts :wrap-memo-props)]
               (let [a (gensym "a")
                     b (gensym "b")
                     wrap-memo-props (mapv #(if (symbol? %)
                                              (keyword (name %))
                                              %)
                                           wrap-memo-props)]
                 (update opts :wrap conj `(kui/memo
                                           (fn [~a ~b]
                                             (= (select-keys ~a ~wrap-memo-props)
                                                (select-keys ~b ~wrap-memo-props))))))
               opts)

        body (if (seq lets)
               `((let [~@lets]
                   ~@body))
               body)]
    `(defnc
       ~type
       ~@(when docstring [docstring])
       ~params
       ~opts
       ~@body)))

;; * HTML Helpers

(defn- conform-style-value [v]
  (cond
    (vector? v)  (->> v (map conform-style-value) (str/join " "))
    (string? v)  v
    (keyword? v) (name v)
    :else        v))

(defn- conform-style [styles]
  (reduce (fn [styles [k v]]
            (assoc styles k (conform-style-value v)))
          {} styles))

(defn html-element
  ([element style-and-children]
   (html-element element style-and-children nil nil))
  ([element style-and-children extra-class extra-style]
   (let [[style children] (if (-> style-and-children first map?)
                            [(first style-and-children) (rest style-and-children)]
                            [nil style-and-children])

         style (if extra-style
                 (merge extra-style style)
                 style)

         props         {}
         [props style] (if-let [k (-> style :key)]
                         [(assoc props :key k) (dissoc style :key)]
                         [props style])
         [props style] (if-let [id (-> style :id)]
                         [(assoc props :id id) (dissoc style :id)]
                         [props style])
         [props style] (if-let [k (-> style :sx)]
                         [(assoc props :sx k) (dissoc style :sx)]
                         [props style])
         [props style] (if-let [k (-> style :ref)]
                         [(assoc props :ref k) (dissoc style :ref)]
                         [props style])
         [props style] (if-let [v (-> style :tab-index)]
                         [(assoc props :tab-index v) (dissoc style :tab-index)]
                         [props style])
         [props style] (if-let [v (-> style :on-click)]
                         [(assoc props :onClick v) (dissoc style :on-click)]
                         [props style])
         [props style] (if-let [v (-> style :onKeyDown)]
                         [(assoc props :onKeyDown v) (dissoc style :onKeyDown)]
                         [props style])
         [props style] (if-let [v (-> style :onKeyPress)]
                         [(assoc props :onKeyPress v) (dissoc style :onKeyPress)]
                         [props style])
         [props style] (if-let [class (if extra-class
                                        (str extra-class " " (-> style :class))
                                        (-> style :class))]
                         [(assoc props :className class) (dissoc style :class)]
                         [props style])
         props         (assoc props :style (conform-style style))]
     `($ ~element ~props ~@children))))

(defmacro div [& style-and-children]
  (html-element :div style-and-children))

(defmacro span [& style-and-children]
  (html-element :span style-and-children))
