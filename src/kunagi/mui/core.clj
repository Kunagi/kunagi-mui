(ns kunagi.mui.core
  (:require
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
