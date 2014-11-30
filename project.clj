(defproject viebel/audyx-toolbet "0.0.1"
  :description "Audyx toolbet"
  :url "https://github.com/viebel/audyx-toolbet"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo}

  ;; We need to add src/cljs too, because cljsbuild does not add its
  ;; source-paths to the project source-paths
  :source-paths ["src/clx"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.andrewmcveigh/cljs-time "0.1.6"]
                 [org.clojure/clojurescript "0.0-2202"]
                 [im.chit/purnam "0.4.3"]
                 [org.clojure/core.async "0.1.278.0-76b25b-alpha"]
                 [org.clojure/clojurescript "0.0-2371"]]

  :plugins [[lein-cljsbuild "1.0.3"]
            [com.keminglabs/cljx "0.4.0"]]

  :hooks [cljx.hooks]
  :cljx {:builds [{:source-paths ["src/cljx"]
                 :output-path "target/classes"
                 :rules :clj}

                {:source-paths ["src/cljx"]
                 :output-path "target/classes"
                 :rules :cljs}]}


  :cljsbuild {:builds {;; This build is only used for including any cljs source
                       ;; in the packaged jar when you issue lein jar command and
                       ;; any other command that depends on it
                       :klozzer
                       {:source-paths ["target/classes"]
                        ;; The :jar true option is not needed to include the CLJS
                        ;; sources in the packaged jar. This is because we added
                        ;; the CLJS source codebase to the Leiningen
                        ;; :source-paths
                        ;:jar true
                        ;; Compilation Options
                        :compiler
                        {:output-to "target/audyx_toolbet.js"
                         :optimizations :simple
                         :pretty-print true}}}})
