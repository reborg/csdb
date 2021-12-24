# CSDB

## Introduction

CSDB is a database engine using CSV (Comma Separated Values) as the main persistent format. You can point CSDB to a folder of CSV files and CSDB will guess shape and relationships between them. You're then free to query the dataset using conventional SQL (or perhaps Datalog, or even predicate logic). For example (after unzipping the 2 files in `datasets/moma-data`):

```clojure
(require '[csdb.core :as csdb]
         '[csdb.sql :refer [select from where]]
         '[csdb.datalog :refer [:-]])

(csdb/default! "datasets/moma-data")
(csdb/sql "SELECT ConstituentID, DisplayName, Nationality
           FROM Artists
           WHERE Artists.Nationality = \"American\"")
;; [...]
;; 132, John Altoon, American
;; [...]

(csdb/datalog "Artists(ConstituentID, DisplayName, Nationality) :-
               Artists(ConstituentID, DisplayName, Nationality), Nationality = 'American'")
;; [...]
;; 132, John Altoon, American
;; [...]
```

## Configuration

CSDB will guess shape and relationships between the CSV files in a folder. This might not be sufficient if the files have very different extensions, formats or header names.

## Large CSV perftest

mkdir datasets/lastfm-data; cd datasets/lastfm-data
curl -O http://mtg.upf.edu/static/datasets/last.fm/lastfm-dataset-1K.tar.gz
curl -O http://mtg.upf.edu/static/datasets/last.fm/lastfm-dataset-360K.tar.gz
tar xvfz lastfm-dataset-1K.tar.gz
tar xvfz lastfm-dataset-360K.tar.gz
cd lastfm-dataset-1K
head -n 1000 userid-timestamp-artid-artname-traid-traname.tsv > small.tsv
cd ..
cd lastfm-dataset-360K
head -n 1000 usersha1-artmbid-artname-plays.tsv > small.tsv
cd ..

## Braindump (WIP)

- The header for each file should really be loaded once and stored
- Allow indexing for multiple columns
- Translation of SQL (or other) instructions into some (clj/calcite/whatnot) executable strategy
  - An example of possible mapping:
    - SELECT -> pick some attributes from resulting relation
    - FROM -> source of initial relation
    - WHERE -> misc constraings on resulting relations
  - without thinking performance one could load everything into relations and use fset ops
    - This introduces all the planning problem: restrict before loading, all other optimizations possible etc.
    - Could have separate strategies: agressive or simple with relations for different use cases?
  - parse sql string -> instaparse -> clj tree AST
    - Easier to traverse/process etc instead of any destructure
    - Better than spec parsing.

## License

Copyright © 2021 JUXT ltd

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
