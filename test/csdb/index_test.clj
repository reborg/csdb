(ns csdb.index-test
  (:require [clojure.test :refer :all]
            [csdb.index :as index]))

(deftest using-the-offset-index-explicitely
  (let [artist-by-name (index/create-offset-index "test/csvs/artists.csv" #"," 1)
        fetch (partial index/fetch artist-by-name "test/csvs/artists.csv" #",")]
    (is (= [["Eugen Batz" 24057] ["Douglas Baz" 25426]] (take 2 artist-by-name)))
    (is (= "American" (get (fetch "Paul Carter") "Nationality")))
    ))
