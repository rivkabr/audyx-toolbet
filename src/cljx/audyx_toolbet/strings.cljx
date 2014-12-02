(ns audyx-toolbet.strings
  (:require  [clojure.string :as string]))


(def regex-char-esc-smap
  (let [esc-chars "()*&^%$#!+?[]{}|\\"]
    (zipmap esc-chars
            (map #(str "\\" %) esc-chars))))
   
(defn escape-string
  [string]
  (->> string
       (replace regex-char-esc-smap)
       (reduce str)))

(defn string-begins? [string query]
  (if (string/blank? query)
    true
    (as-> query $
         (string/lower-case $)
         (escape-string $)
         (re-pattern (str "^" $))
         (re-find $ (string/lower-case string))
         (not (nil? $)))))

