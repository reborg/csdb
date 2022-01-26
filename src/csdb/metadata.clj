(ns csdb.metadata
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [csdb.encoding :as enc])
   (:import [java.io RandomAccessFile File]))

(defn ordered-frequencies
  "Like frequencies, bu with keys ordered by highest value."
  [coll]
  (reduce
   (fn [counts x]
     (let [counts (if (= ::init counts)
                    (sorted-map-by (fn [k1 k2]
                                     (compare [(get counts k2) k2]
                                              [(get counts k1) k1])))
                    counts)]
       (assoc counts x (inc (get counts x 0)))))
   ::init
   coll))

(defn guess-separator
  "Grab the most frequent punctuation, excluding
  things like double quote, dash, underscore or
  dot from the candidates."
  [s]
  (->> (str/replace s #"[\w\"\-\_\.]" "")
       seq
       ordered-frequencies
       ffirst
       str))

(defn headers-from-file
  [file]
  (with-open [raf (RandomAccessFile. (io/file file) "r")]
     (-> raf .readLine enc/to-utf8 enc/strip-bom)))

(defn file-name [file]
  (.replaceFirst (.getName (io/file file)) "[.][^.]+$" ""))

(defn from-file
  [file]
  (let [header (headers-from-file file)
        separator (guess-separator header)
        attributes (->> (str/split header (re-pattern separator))
                        (map-indexed #(vector %2 %1))
                        (into {}))]
    {:relation (file-name file)
     :attributes attributes}))
