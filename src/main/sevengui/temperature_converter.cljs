(ns sevengui.temperature-converter
  (:require
    [reagent.core :as r]))

;; This will resolve some of the rounding issue Ex. 23.000000000000004 to 23
(defn round [num]
  (/ (Math/round (* 100
                   (+ num (.-EPSILON js/Number))))
    100))

(defn celsius->fahrenheit [celsius]
  (round (+ (* celsius (/ 9 5)) 32)))

(defn fahrenheit->celsius [fahrenheit]
  (round (* (- fahrenheit 32) (/ 5 9))))

(defn temperature-converter []
  (let [state (r/atom {:fahrenheit ""
                       :celsius ""})
        handle-change (fn [conversion-map e]
                        (let [val (.. e -target -value)]
                          (swap! state assoc (:from conversion-map) val)
                          (when (not (js/isNaN val))
                            (swap! state assoc (:to conversion-map) ((:convert-fn conversion-map) val)))))]
    (fn []
      [:div
       [:input {:type "text"
                :aria-label "celsius"
                :name "celsius"
                :value (:celsius @state)
                :on-change (partial handle-change {:from :celsius
                                                   :to :fahrenheit
                                                   :convert-fn celsius->fahrenheit})}]
       [:span "Celsius = "]
       [:input {:type "text"
                :aria-label "fahrenheit"
                :name "fahrenheit"
                :value (:fahrenheit @state)
                :on-change (partial handle-change {:from :fahrenheit
                                                   :to :celsius
                                                   :convert-fn fahrenheit->celsius})}]
       [:span "Fahrenheit"]])))

(comment
  (/ (Math/round (* 100
                   (+ 23.000000000000004 (.-EPSILON js/Number))))
    100))
