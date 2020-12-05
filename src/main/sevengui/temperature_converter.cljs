(ns sevengui.temperature-converter
  (:require
    [reagent.core :as r]))

;; This will resolve some of the rounding issue Ex. 23.000000000000004 to 23
;; while keeping float like (= 10.8 (round 10.8))
(defn round [num]
  (/ (Math/round (* 100
                   (+ num (.-EPSILON js/Number))))
    100))

(defn numeric-val [num]
  (if (js/isNaN num)
    nil
    num))

(defn celsius->fahrenheit [celsius]
  (some-> celsius
    (numeric-val)
    (#(round (+ (* % (/ 9 5)) 32)))))

(defn fahrenheit->celsius [fahrenheit]
  (some-> fahrenheit
    (numeric-val)
    (#(round (* (- % 32) (/ 5 9))))))

(defn temperature-converter []
  (let [state (r/atom {:fahrenheit ""
                       :celsius ""})]
    (fn []
     [:div
      [:input {:type "text"
               :aria-label "celsius"
               :name "celsius"
               :value (:celsius @state)
               :on-change #(let [val (.. % -target -value)]
                             (swap! state assoc
                               :celsius val
                               :fahrenheit (celsius->fahrenheit val)))}]

      [:span "Celsius = "]
      [:input {:type "text"
               :aria-label "fahrenheit"
               :name "fahrenheit"
               :value (:fahrenheit @state)
               :on-change #(let [val (.. % -target -value)]
                             (swap! state assoc
                               :fahrenheit val
                               :celsius (fahrenheit->celsius val)))}]
      [:span "Fahrenheit"]])))

(comment
  (round 33.0000004)
  (/ (Math/round (* 100
                   (+ 23.000000000000004 (.-EPSILON js/Number))))
    100))
