(ns csdb.perf
  (:require
   [clojure.test :refer :all]
   [criterium.core :refer [bench quick-bench quick-benchmark benchmark]]
   [csdb.core :as csdb]))

(defmacro b [expr] `(first (:mean (quick-benchmark ~expr {}))))

(deftest filtering-files-bench
  (is (pos? (count (csdb/filter-dir "test/csdb" (csdb/file-filter "clj")))))
  (is (pos? (count (csdb/filter-dir "test/csvs" (csdb/file-filter "csv"))))))
