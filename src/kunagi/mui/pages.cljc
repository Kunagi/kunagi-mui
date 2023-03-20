(ns kunagi.mui.pages
  #?(:cljs (:require-macros [kunagi.mui.pages :refer [def-page]]))
  (:require
   [clojure.string :as str]
   [camel-snake-kebab.core :as csk]
   [kunagi.utils.definitions :as definitions]))

(defn pages []
  (definitions/definitions :kunagi.mui/page))

(defn pages-in-router-order []
  (->> (pages)
       (sort-by :router-path)
       reverse))

(defn- coerce-page-path [thing]
  (if (string? thing)

    thing

    (->> thing
         (map (fn [path-element]
                (or
                 (when (keyword? path-element)
                   (str ":" (-> path-element csk/->camelCaseString)))

                 (when (vector? path-element)
                   (when-let [sym (or (-> path-element second :doc-schema/symbol)
                                      (-> path-element second :subdoc-schema/symbol))]
                     (str ":" sym "Id")))

                 (str path-element))))
         (str/join "/")
         (str "/ui/"))))

(comment
  (coerce-page-path "/ui/book/:book")
  (coerce-page-path ["book" :book-id])
  (coerce-page-path []))

(defn init-page [page]
  ;; (tap> reg-page)
  ;; (prn "reg-page" page)
  (let [k (keyword (-> page :page/namespace) (-> page :page/symbol))
        page (assoc page
                    :id k
                    :router-path (coerce-page-path (-> page :path)))]
    page))

(defmacro def-page  [sym opts]
  (let [symbol-name (-> sym name)
        calling-namespace-name (name (ns-name *ns*))
        id (str calling-namespace-name "/" symbol-name)]
    (assoc opts
           :page/id id
           :page/symbol symbol-name
         ;; (keyword schema-name "namespace") calling-namespace-name
           )
    (definitions/macro-gen-def sym opts  :kunagi.mui/page `init-page)))
