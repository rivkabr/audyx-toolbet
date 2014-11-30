(ns audyx-toolbet.functions
  #+cljs 
  (:use-macros [cljs.core.async.macros :only [go go-loop]])
   
  (:require [clojure.string :as string]
            #+cljs
            [cljs.core.async :refer [<! timeout alts!]]
            #+clj
            [clojure.core.async :refer [<! timeout alts! go go-loop]]))

(defn memoize-async [f]
  (let [mem (atom {})]
    (fn [& args]
      (go
        (if-let [e (find @mem args)]
          (val e)
          (let [ret (<! (apply f args))]
            (swap! mem assoc args ret)
            ret))))))

(defn go-map [f coll]
  (let [chans (map f coll)
        chan->coll (zipmap chans coll)]
    (go-loop [res {} channels chans]
             (if (empty? channels)
               (map res coll)
               (let [[x c] (alts! channels)]
                 (recur (assoc res (chan->coll c) x)
                        (remove #{c} channels)))))))

(defn vec->map [vec]
  (into {} vec))

(defn go-map-2d-vec [f m]
    (go-map (fn[[k id]] (go [k (<! (f id))])) m))

(defn go-map-object [f m]
  (go
    (vec->map (<! (go-map-2d-vec f m)))))

(defn go-map-to-object[f lst]
  (go
    (zipmap lst (<! (go-map f lst)))))

(defn wait-for-msg [c msg]
  (go
    (loop []
      (when-not (= msg (<! c))
        (recur)))))

(defn wait-for-condition [f interval-in-msec]
  (go-loop []
    (when-not (f)
      (<! (timeout interval-in-msec))
      (recur))))


(defn get-extension-file [filename]
  (last (string/split filename #"\.")))

(defn string-in? [search string]
  (not (nil? (re-find (re-pattern search) string))))


(defn parallel [chans]
  (let [channel-indexes (zipmap chans (range (count chans)))]
    (go-loop [res (vec (range (count chans))) channels chans]
             (if (empty? channels)
               res
               (let [[data c] (alts! channels)]
                 (recur (assoc res (channel-indexes c) data) (remove #{c} channels)))))))
