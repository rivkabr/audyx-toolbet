(ns audyx-toolbet.test-functions
  (:use [audyx-toolbet.functions]
        [midje.sweet])
  (:require [clojure.core.async :refer [<!! go]]))

(defn async-square [x]
  (go
    (* x x)))

(facts "Utils/Functions"
       (fact "parallel" 
             (<!! (parallel [])) => []
             (<!! (parallel [(async-square 1) (async-square 2) (async-square 3)])) => [1 4 9])
       (fact "memoize-async"
             (<!! ((memoize-async async-square) 4)) => 16
             (<!! ((memoize-async async-square) 4)) => 16)
       (tabular
         (fact "go-map"
                (<!! (go-map async-square ?in)) => ?out)
         ?in ?out
         [] []
         [1 2 3] [1 4 9])
       (tabular
         (fact "go-map-object"
                (<!! (go-map-object async-square ?in)) => ?out)
         ?in ?out
         {} {}
         {:a 1 :b 2 :c 3} {:a 1 :b 4 :c 9})
       (tabular
         (fact "go-map-to-object"
                (<!! (go-map-to-object async-square ?in)) => ?out)
         ?in ?out
         [] {}
         [1 2 3] {1 1 2 4 3 9})
       (tabular
         (fact "get-extension-file"
                (get-extension-file ?in) => ?out)
         ?in ?out
         "name.jpg" "jpg"
         "name.min.js" "js"
         "name_pre.min.css" "css")
       (tabular
         (fact "string-in?"
                (string-in? ?in "fir cbnfi fifiowsebfibewfew 0 79843 bv re8") => ?out)
         ?in ?out
         "" true
         "bonjour" false
         "fi fi" true))

