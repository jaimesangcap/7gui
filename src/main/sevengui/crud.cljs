(ns sevengui.crud
  (:require
    [reagent.core :as r]
    [clojure.string :as str]))

(def base-spacing "8px")

;; how to remove warning about unique key here? react think its a list
(defn form-field [& children]
  [:div {:style {:display "flex" :justify-content "space-between"}}
   (into children)])

(defn person-item [{:keys [on-select selected person]}]
  [:div {:on-click #(on-select person)
         :style {:background-color (when selected "blue")}}
   (str (:surname person) ", " (:name person))])

(def person-id (atom 0))
(defn new-id []
  (swap! person-id inc))

(defn new-person [p]
  (assoc p :id (new-id)))

(defn clear-form [state]
  (swap! state assoc :form {}))

(defn crud []
  (let [people (r/atom {})
        state (r/atom {:form {:name ""
                              :surname ""}
                       :selected-person nil
                       :filter ""})]
    (fn []
      [:div
       [:form
        [:label {:for "filter"} "Filter prefix: "]
        [:input {:type "text"
                 :id "filter"
                 :value (:filter @state)
                 :on-change #(swap! state assoc :filter (.. % -target -value))}]]
       [:div {:style {:display "flex"
                      :margin-top base-spacing}}
        [:div {:data-testid "listbox"
               :style {:border "1px solid #eee"
                       :flex 1
                       :padding base-spacing}}
         (for [p (filter #(str/starts-with? (:surname %) (:filter @state))
                         (vals @people))]
           ^{:key (:id p)} [person-item {:person p
                                         :on-select (fn [person]
                                                      (swap! state assoc
                                                             :selected-person person
                                                             :form person))
                                         :selected (= (:id p) (:id (:selected-person @state)))}])]

        [:div {:style {:padding base-spacing}}
         [:div {:style {:display "flex"
                        :flex-direction "column"
                        :padding base-spacing}}
          [form-field
           [:label {:for "name"} "Name: "]
           [:input {:type "text"
                    :id "name"
                    :value (get-in @state [:form :name])
                    :on-change #(swap! state assoc-in [:form :name] (.. % -target -value))}]]
          [form-field
           [:label {:for "surname"} "Surname: "]
           [:input {:type "text"
                    :id "surname"
                    :value (get-in @state [:form :surname])
                    :on-change #(swap! state assoc-in [:form :surname] (.. % -target -value))}]]]]]
       [:div {:style {:display "flex"}}
        [:button {:on-click (fn []
                              (let [id (new-id)]
                                (swap! people assoc id (assoc (:form @state) :id id))
                                (clear-form state)))}
         "Create"]
        [:button {:disabled (nil? (:selected-person @state))
                  :on-click (fn []
                              (swap! people update (get-in @state [:selected-person :id]) #(merge % (:form @state))))}
         "Update"]
        [:button {:disabled (nil? (:selected-person @state))
                  :on-click (fn [_e]
                              (swap! people dissoc (get-in @state [:selected-person :id]))
                              (clear-form state))} "Delete"]]])))


(comment
  (conj [{:name "John"}] {:name "Doe"}))
