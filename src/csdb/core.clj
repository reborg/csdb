(ns csdb.core
  (:require
   [clojure.java.io :as io]
   [csdb.index :as index]
   [instaparse.core :as ip]
   [clojure.core.match :refer [match]])
  (:import [java.io File FileFilter])
  (:refer-clojure :exclude [compile]))

(def ^:dynamic *db* nil)

(defmacro with-db [db & forms]
  `(binding [*db* ~db]
    ~@forms))

(defn filename [^File f]
  (let [^String fname (.getName f)
        dot (.lastIndexOf fname ".")]
    (if (pos? dot)
      (.substring fname 0 dot)
      fname)))

(defn extension [^File f]
  (let [^String fname (.getName f)
        dot (.lastIndexOf fname ".")]
    (if (pos? dot)
      (.substring fname (inc dot))
      nil)))

(defn file-filter [ext]
  (proxy [FileFilter] []
    (accept [^File f]
      (if (.isFile f)
        (= ext (extension f))
        false))))

(def csv-filter
  (file-filter "csv"))

(defn filter-dir
  ([folder]
   (filter-dir folder csv-filter))
  ([folder ^FileFilter file-filter]
   (.listFiles (io/file folder) file-filter)))

(defn open [folder]
  (into
   {}
   (map (juxt filename index/reader))
   (filter-dir folder)))

(def sql-parser
  (ip/parser
   (io/resource "SQL2011.ebnf")
   :auto-whitespace (ip/parser "whitespace = #'\\s+' | #'\\s*--[^\r\n]*\\s*' | #'\\s*/[*].*?([*]/\\s*|$)'")
   :string-ci true))

(defn dispatch [[term :as form]] term)
(defmulti compile dispatch)
(defmethod compile nil [form] (println "### should not compile nil"))
(defmethod compile :regular_identifier [[_ const]] const)

(defmethod compile :default
  [[term :as form]]
  (let [body (peek form)]
    (if (and body (vector? body))
      (compile body)
      form)))

(defmethod compile :query_specification
  [[_ kind [_ & select-list] [_ from-clause where-clause :as table-expr] :as form]]
  (let [attributes (map compile select-list)
        from (compile from-clause)
        where (compile where-clause)]
    (println "###" where)
    :here))

(defn sql
  ([s]
   (sql s :just-ast false))
  ([s & {:as opts}]
   (let [ast (sql-parser s :start :directly_executable_statement)]
     (if (:just-ast opts)
       ast
       (compile ast)))))
