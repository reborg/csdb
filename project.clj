(defproject csdb "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [com.univocity/univocity-parsers "2.9.1"]
                 [instaparse "1.4.10"]
                 [org.clojure/core.match "1.0.0"]]
  :profiles {:dev {:dependencies [[criterium  "0.4.6"]]
                   :plugins []}}
  :global-vars {*warn-on-reflection* true}
  :repl-options {:init-ns csdb.core})
