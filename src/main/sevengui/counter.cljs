(ns sevengui.counter
  (:require
    [reagent.core :as r]))

(defn counter []
  (let [value (r/atom 0)]
    (fn []
      [:div
       [:input {:type "text" :value @value :disabled true}]
       [:button {:type "button" :on-click #(swap! value inc)} "count"]])))
