(ns sevengui.flight-booker
  (:require
    [reagent.core :as r]
    [clojure.string :as str]))

(defn format-date [date]
  (-> (.toISOString date)
    (str/split #"T")
    (first)))


(defn flight-booker [{:keys [default-date]
                      :or {default-date (js/Date.)}}]
  (let [state (r/atom {:selected-option :one-way
                       :start-date default-date
                       :return-date default-date})]
    (fn []
      [:div
       [:div
        [:select {:on-change #(swap! state assoc
                                :selected-option (.. % -target -value))}
         [:option {:value :one-way} "one-way flight"]
         [:option {:value :return} "return flight"]]]
       [:div
        [:input {:type "text"
                 :aria-label :start-date
                 :name :start-date
                 :value (format-date (:start-date @state))}]]
       [:div
        [:input {:type "text"
                 :aria-label :return-date
                 :name :return-date
                 :value (format-date (:return-date @state))
                 :disabled (not= :return (:selected-option @state))}]]
       [:button {:type "submit"} "book"]])))
