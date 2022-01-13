(ns csdb.core-test
  (:require [clojure.test :refer :all]
            [csdb.core :as csdb :refer [with-db sql]]))

(deftest sanity
  (let [db (csdb/open "test/csvs")
        stmt "SELECT ConstituentID, DisplayName, Nationality
             FROM Artists
             WHERE Artists.Nationality = \"American\""]
    (testing "perform the default indexing"
      (is (= ["artworks" "artists"] (keys db))))
    (testing "generate the AST"
      (let [[kind & ast] (with-db db (sql stmt :just-ast true))]
        (is (= :directly_executable_statement kind))))
    (testing "matching on stuff"
      (let [out (with-db db (sql stmt))]
        (is (= :here out))))))

(def reference-ast-as-documentation
  [:directly_executable_statement
   [:direct_select_statement__multiple_rows
    [:query_expression
     [:query_expression_body
      [:query_term
       [:query_primary
        [:query_expression_body
         [:query_term
          [:query_primary
           [:query_specification
            "SELECT"
            [:select_list
             [:derived_column
              [:numeric_value_expression
               [:term
                [:factor
                 [:column_reference
                  [:basic_identifier_chain
                   [:identifier_chain
                    [:identifier
                     [:regular_identifier "ConstituentID"]]]]]]]]]
             [:derived_column
              [:numeric_value_expression
               [:term
                [:factor
                 [:column_reference
                  [:basic_identifier_chain
                   [:identifier_chain
                    [:identifier
                     [:regular_identifier "DisplayName"]]]]]]]]]
             [:derived_column
              [:numeric_value_expression
               [:term
                [:factor
                 [:column_reference
                  [:basic_identifier_chain
                   [:identifier_chain
                    [:identifier
                     [:regular_identifier "Nationality"]]]]]]]]]]
            [:table_expression
             [:from_clause
              "FROM"
              [:table_reference_list
               [:table_factor
                [:table_primary
                 [:table_name
                  [:identifier [:regular_identifier "Artists"]]]]]]]
             [:where_clause
              "WHERE"
              [:search_condition
               [:boolean_value_expression
                [:boolean_term
                 [:boolean_factor
                  [:boolean_test
                   [:comparison_predicate
                    [:row_value_special_case
                     [:column_reference
                      [:basic_identifier_chain
                       [:identifier_chain
                        [:identifier [:regular_identifier "Artists"]]
                        [:identifier
                         [:regular_identifier "Nationality"]]]]]]
                    [:comparison_predicate_part_2
                     [:equals_operator "="]
                     [:row_value_special_case
                      [:column_reference
                       [:basic_identifier_chain
                        [:identifier_chain
                         [:identifier
                          [:delimited_identifier
                           "\"American\""]]]]]]]]]]]]]]]]]]]]]]]]])
