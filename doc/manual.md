# csdb - Comma Separated DB manual

## Scratch

* data.csv or clojure-csv are for reading a single csv file in one go, perhaps lazily. csdb goal is to index a group of csv and work more like a db using csv as the native format.
* It requires an indexing pre-phase, like the Iota library, but again, the aim is not to necessarily parse the entire file after that, but more making random access to it.
* The other interesting part is the implicit joins that the library can provide.
* It could support SQL like syntax, or other relational languages, including pure first order.

## Getting started

## Usage

```clojure
(require '[csdb.core :as csdb]
         '[csdb.sql :refer [select from where]]
         '[csdb.datalog :refer [:-]])

(def db (csdb/open "csv/folder/"))
(csdb/default! db)

(select id, name, year (from courses)
  (where id = 133))
;; 101,programming languages,2021

(:- (course id, name, year) (= id 133))
;; 101,programming languages,2021
```
