(defproject gen-czech-birth-code "0.1.0-SNAPSHOT"
  :description "REST API that generates Czech birth codes!"
  :url "cz-birth-code.herokuapp.com/v1/b-code/generate"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 ; Compojure - A basic routing library
                 [compojure "1.6.1"]
                 ; Our Http library for client/server
                 [http-kit "2.3.0"]
                 ; Ring defaults - for query params etc
                 [ring/ring-defaults "0.3.2"]
                 ; Clojure data.JSON library
                 [org.clojure/data.json "0.2.6"]
                 ; Heroku API
                 [lein-heroku "0.5.3"]]
  :main ^:skip-aot gen-czech-birth-code.core
  :aot [gen-czech-birth-code.core]
  :target-path "target/%s"
  :profiles {:uberjar {:main gen-czech-birth-code.core, :aot :all}}
  :heroku {:app-name "cz-birth-code"
           :jdk-version "1.8"
           :include-files ["target/uberjar/gen-czech-birth-code-0.1.0-SNAPSHOT-standalone.jar"]
           :process-types {"web" "java -jar target/uberjar/gen-czech-birth-code-0.1.0-SNAPSHOT-standalone.jar"}})