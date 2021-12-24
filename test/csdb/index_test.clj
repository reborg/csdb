(ns csdb.index-test
  (:require [clojure.test :refer :all]
            [csdb.index :as index]))

(deftest using-the-offset-index-explicitely
  (let [artist-by-name (index/create-offset-index "test/csvs/artists.csv" #"," 1)]
    (is (= "American" ((index/fetch
                        artist-by-name
                        "test/csvs/artists.csv"
                        #","
                        "Paul Carter")
                       "Nationality")))))
