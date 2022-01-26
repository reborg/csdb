(ns csdb.encoding
  (:require [clojure.string :as str]))

(defn to-utf8 [^String s]
  (String. (.getBytes s "ISO-8859-1") "UTF-8"))

(defn strip-bom [^String s]
  (str/replace s #"^\uFEFF" ""))
