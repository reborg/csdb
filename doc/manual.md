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

## Digression on "Transactions"

HasToReport(party) := FC(party) AND Equity(product) AND Transaction(party, product)

Note that the party and the product variables, once substituted in the predicate "Transaction" needs to appear in the transaction table. So for example Dparty never appears in a transaction they can never be responsible for reporting of course. But looking at the transaction table, joined with transaction-parties and transaction=products I can seE:

JOIN tx, tx-parties, tx-products:
tx-id party-id product-id
1 party1 equity (here the party was FC)
1 party2 equity (here the party was not FC)

tx-id timestamp venue reported?
1     now       DTCC  false
2     yesterday CME   true

"transaction-parties: which parties attended the tx"
tx-id party-id FC
1     1        true
1     2        false
2     1        true
2     2        true
2     3        false

"transacted-products: what products were involved?"
tx-id product-id
1     2
2     1
2     3

table-party
id name
1  Aparty
2  Bparty
3  Cparty

table-venue
id name
1  CME
2  DTCC

table-time-frame
id name
1  T+5min
2  T+10min
3  T+1D

table-product-types
id name type
1  p1   equity
2  p2   derivative
3  p3   future
