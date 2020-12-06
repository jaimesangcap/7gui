(ns sevengui.flight-booker
  (:require
    [reagent.core :as r]
    [clojure.string :as str]))

(defn format-date [date]
  (-> (.toISOString date)
    (str/split #"T")
    (first)))

(defn flight-date [flight]
  (str
    (:start-date flight)
    (when (= (:selected-option flight) "return")
      (str " to " (:return-date flight)))))

;; Keeping the date validation simple for now as I'm not using any library
(defn valid-date? [date-string]
  (not (nil? (re-matches #"\d{4}-\d{2}-\d{2}" date-string))))

(defn valid-date-range? [start end]
  (and
    (not (nil? start))
    (not (nil? end))
    (>= (js/Date. end) (js/Date. start))))

(defn valid-booking? [{:keys [start-date return-date selected-option]}]
  (and (valid-date? start-date)
       (or (= selected-option "one-way")
           (and (valid-date? return-date)
                (valid-date-range? start-date return-date)))))


(defn flight-booker [{:keys [default-date]
                      :or {default-date (format-date (js/Date.))}}]
  (let [state (r/atom {:selected-option "one-way"
                       :start-date default-date
                       :return-date default-date})
        handle-submit (fn [e]
                        (.preventDefault e)
                        (swap! state assoc
                          :success-message (str "You have booked a " (:selected-option @state) " flight on " (flight-date @state))))]
    (fn []
      [:form {:on-submit handle-submit}
       (when (:success-message @state) [:div {:testid "success-message"} (:success-message @state)])
       [:div
        [:select {:on-change #(swap! state assoc :selected-option (.. % -target -value))}
         [:option {:value "one-way"} "one-way flight"]
         [:option {:value "return"} "return flight"]]]
       [:div
        [:input {:type "text"
                 :style {:background-color (when-not (valid-date? (:start-date @state)) "red")}
                 :aria-label :start-date
                 :name :start-date
                 :value (:start-date @state)
                 :on-change #(swap! state assoc :start-date (.. % -target -value))}]]
       [:div
        [:input {:type "text"
                 :style {:background-color (when (and (not (valid-date? (:return-date @state)))
                                                      (= "return" (:selected-option @state)))
                                             "red")}
                 :aria-label :return-date
                 :name :return-date
                 :value (:return-date @state)
                 :on-change #(swap! state assoc :return-date (.. % -target -value))
                 :disabled (not= "return" (:selected-option @state))}]]
       [:button {:type "submit"
                 :disabled (not (valid-booking? @state))}
        "book"]])))
