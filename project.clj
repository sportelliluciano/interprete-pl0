(defproject interprete-pl0 "0.1.0"
  :description "TP Final - Lenguajes Formales: Intérprete de PL/0"
  :url "https://github.com/sportelliluciano/interprete-pl0"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :main ^:skip-aot interprete-pl0.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
