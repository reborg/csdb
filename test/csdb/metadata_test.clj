(ns csdb.metadata-test
  (:require [clojure.test :refer :all]
            [csdb.metadata :as metadata]))

(deftest extracting-metadata-test
  (testing "load just the first line from a file"
    (is (= "ConstituentID,DisplayName,ArtistBio,Nationality,Gender,BeginDate,EndDate,Wiki QID,ULAN"
           (metadata/headers-from-file "test/csvs/artists.csv"))))
  (testing "guessing the separator"
    (is (= "," (metadata/guess-separator "Hay,Hoy,Fee,Baz")))
    (is (= "," (metadata/guess-separator "ConstituentID,DisplayName,ArtistBio,Nationality,Gender,BeginDate,EndDate,Wiki QID,ULAN"))))
  (testing "it gets all the necessary info"
    (is (= {:relation "artists"
            :attributes {"ConstituentID" 0 "DisplayName" 1 "ArtistBio" 2 "Nationality" 3
                         "Gender" 4 "BeginDate" 5 "EndDate" 6 "Wiki QID" 7 "ULAN" 8}}
           (metadata/from-file "test/csvs/artists.csv")))))
