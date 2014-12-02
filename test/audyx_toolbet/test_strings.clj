(ns audyx-toolbet.test-strings
  (:use [audyx-toolbet.strings]
        [midje.sweet]))

(facts "Utils/Strings"
	(tabular
       (fact "string-begins?" (string-begins? ?a ?b) =>  ?res)
       		?a ?b ?res
       		"" "query" false
       		"string" "" true
       		"string" "string" true
       		"string" "str" true
       		"mystring" "string" false)
)
