(defproject curbstomp "0.1.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [http-kit "2.1.19"]
                 [cheshire "5.5.0"]]
  :dev-dependencies [[org.clojure/tools.nrepl "0.2.10"]]
  :plugins [[cider/cider-nrepl "0.9.0-SNAPSHOT"]]
  :main ^:skip-aot curbstomp.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
