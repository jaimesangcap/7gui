(ns sevengui.crud
  (:require
    [reagent.core :as r]
    [clojure.string :as str]))

(def base-spacing "8px")

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

(defn crud []
  (let [people (r/atom {})
        state (r/atom {:form {:name ""
                              :surname ""}
                       :selected-person nil
                       :filter ""})]
    (fn []
      [:div
       [:form
        [:span "Filter prefix: "]
        [:input {:type "text"
                 :value (:filter @state)
                 :on-change #(swap! state assoc :filter (.. % -target -value))}]]
       [:div {:style {:display "flex"
                      :margin-top base-spacing}}
        [:div {:style {:border "1px solid #eee"
                       :flex 1
                       :padding base-spacing}}
         (for [p (filter #(str/starts-with? (:surname %) (:filter @state))
                         (vals @people))]
           (do (js/console.log "selected " (= p (:selected-person @state)))
               ^{:key (:id p)} [person-item {:person p
                                             :on-select (fn [person]
                                                          (js/console.log " person " person)
                                                          (swap! state assoc
                                                                 :selected-person person
                                                                 :form person))
                                             :selected (= (:id p) (:id (:selected-person @state)))}]))]

        [:div {:style {:padding base-spacing}}
         [:div {:style {:display "flex"
                        :flex-direction "column"
                        :padding base-spacing}}
          [form-field
           [:label "Name: "]
           [:input {:type "text"
                    :value (get-in @state [:form :name])
                    :on-change #(swap! state assoc-in [:form :name] (.. % -target -value))}]]
          [form-field
           [:label "Surname: "]
           [:input {:type "text"
                    :value (get-in @state [:form :surname])
                    :on-change #(swap! state assoc-in [:form :surname] (.. % -target -value))}]]]]]
       [:div {:style {:display "flex"}}
        [:button {:on-click (fn []
                              (let [id (new-id)]
                                (swap! people assoc id (assoc (:form @state) :id id))
                                (swap! state assoc :form {})))}
         "Create"]
        [:button {:disabled (nil? (:selected-person @state))
                  :on-click (fn []
                              (swap! people update (get-in @state [:selected-person :id]) #(merge % (:form @state))))}
         "Update"]
        [:button {:disabled (nil? (:selected-person @state))
                  :on-click (fn [_e]
                              (swap! people dissoc (get-in @state [:selected-person :id]))
                              (swap! state assoc :form {}))} "Delete"]]])))


(comment
  (conj [{:name "jaime"}] {:name "mariely"}))
