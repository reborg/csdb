(ns csdb.index
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [csdb.encoding :as enc])
   (:import [java.io RandomAccessFile File]
            [com.univocity.parsers.csv CsvParserSettings CsvParser]
            [com.univocity.parsers.common.processor RowListProcessor]))

(defn create-offset-index
  "Create a hashmap index from value at key-index to file
  offsets. Use the hashmap to read a line from the file at the
  given index using RandomAccessFile::seek. Take optional key-fn
  to process keys."
  ([file sep key-index]
   (create-offset-index file sep key-index identity))
  ([^File file sep key-index key-fn]
   (with-open [raf (RandomAccessFile. (io/file file) "r")]
     (loop [offset (.getFilePointer raf)
            line (.readLine raf)
            index (transient {})]
       (if line
         (let [k (nth (string/split line sep) key-index)]
           (recur
            (.getFilePointer raf)
            (.readLine raf)
            (assoc! index (key-fn k) offset)))
         (persistent! index))))))

(def parser-settings
  (doto
    (CsvParserSettings.)
    (.setLineSeparatorDetectionEnabled true)
    (-> (.getFormat) (.setNormalizedNewline \newline))
    (.setRowProcessor (RowListProcessor.))
    (.setHeaderExtractionEnabled false)
    (.setIgnoreLeadingWhitespaces true)
    (.setIgnoreTrailingWhitespaces true)
    (.setNumberOfRecordsToRead 1)))

(defn line-parser [^String line]
  (let [^CsvParser parser (CsvParser. parser-settings)]
   (try
    (.beginParsing parser (io/reader (.getBytes line "ISO-8859-1")))
    (.parseNext parser)
    (finally
     (.stopParsing parser)))))

(defn fetch
  "Given a an offset-index created with create-offset-index,
  fetch the value for key `k` in file `file` and separator
  regexp `sep`. Prefer using csv/reader unless you need to
  further process the offset-index. Take an optional key-fn to
  process keys, presumably the same that was used to create the
  offset-index or `identity` by default."
  ([offset-index file sep k]
   (fetch offset-index file sep k identity))
  ([offset-index ^File file sep k key-fn]
   (with-open [raf (RandomAccessFile. (io/file file) "r")]
     (when-let [offset (offset-index (key-fn k))]
       (let [header (.readLine (doto raf (.seek 0)))
             line (.readLine (doto raf (.seek offset)))]
         (zipmap
          (string/split (enc/to-utf8 header) sep)
          (into [] (line-parser line))))))))

(defn reader*
  "Create a reader that retrieves a map of field names to
  field values from a `sep`-separated file at given key `k`.
  The `key-index` is used for the initial generation of the index
  The original file never resides in memory in full."
  ([file]
   (reader* file #"," 0 identity))
  ([file sep]
   (reader* file sep 0 identity))
  ([file sep key-index]
   (reader* file sep key-index identity))
  ([file sep key-index key-fn]
   (let [offset-index (create-offset-index file sep key-index key-fn)]
     (fn
       ([] "dude")
       ([k] (fetch offset-index file sep k key-fn))))))

(def reader (memoize reader*))
