(ns kunagi.mui.api
  (:require
   [kunagi.mui.core :as core]))

(defmacro $ [type & args] `(core/$ ~type ~@args))
(defmacro <> [& children] `(core/<> ~@children))
(defmacro create-context [& body] `(core/create-context ~@body))
(defmacro provider [opts & children] `(core/provider ~opts ~@children))
(defmacro use-context [& body] `(core/use-context ~@body))
(defmacro use-state [& body] `(core/use-state ~@body))
(defmacro use-effect [& body] `(core/use-effect ~@body))
(defmacro use-memo [& body] `(core/use-memo ~@body))

(defmacro def-ui [& body] `(core/def-ui ~@body))
(defmacro div [& body] `(core/div ~@body))
(defmacro span [& body] `(core/span ~@body))

(defmacro stack [& children] `($ Stack {:spacing 1} ~@children))
(defmacro stack-0 [& children] `($ Stack {:spacing 0} ~@children))
(defmacro stack-1 [& children] `($ Stack {:spacing 1} ~@children))
(defmacro stack-2 [& children] `($ Stack {:spacing 2} ~@children))
(defmacro stack-3 [& children] `($ Stack {:spacing 3} ~@children))
(defmacro stack-4 [& children] `($ Stack {:spacing 4} ~@children))

(defmacro flex [& children] `($ Stack {:spacing 1 :direction "row"} ~@children))
