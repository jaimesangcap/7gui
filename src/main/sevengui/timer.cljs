(ns sevengui.timer
  (:require
    [reagent.core :as r]))

(defn progress-bar [{:keys [progress]
                     :or {progress 0}}]
  [:div {:style {:width "100%"
                 :height "20px"
                 :position "relative"
                 :background-color "#dedede"}}
   [:div {:style {:width (str (min progress 100) "%")
                  :height "100%"
                  :background-color "skyblue"
                  :position "absolute"}}]])

(defn slider []
  (fn [{:keys [min max value on-change]}]
    [:input {:type "range"
             :value value
             :min min
             :max max
             :on-change on-change}]))

(defn delta-time [start-time]
  (- (js/Date.now) start-time))

(defn progress-percentage [state]
  (* (/ (/ (:elapsed-time state) 1000) (:duration state)) 100))

(defn humanize-elapsed-time [elapsed]
  (let [elapsed-in-secs (/ elapsed 1000)
        secs (Math/floor (mod elapsed-in-secs 60))
        ms (subs (.toFixed (mod elapsed-in-secs 1) 3) 2)]
    (str secs "." ms "s")))

(defn timer []
  (let [timer-handle (r/atom nil)
        state (r/atom {:elapsed-time 0
                       :duration 30
                       :start-time (js/Date.now)})
        start-timer (fn []
                      (reset! timer-handle (js/setInterval (fn [] (swap! state #(assoc % :elapsed-time (+ (:elapsed-time %) (delta-time (:start-time %)))
                                                                                         :start-time (js/Date.now)))) 25)))
        reset-timer #(swap! state assoc
                            :elapsed-time 0
                            :start-time (js/Date.now))
        clear-timer (fn []
                      (js/clearInterval @timer-handle)
                      (reset! timer-handle nil))]

    (r/create-class
      {:display-name "timer"
       :reagent-render
       (fn []
         [:div
          [:div
           [:span "Elapsed Time: "]
           [progress-bar {:progress (progress-percentage @state)}]]
          [:span (humanize-elapsed-time (:elapsed-time @state))]
          [:div
           [:span "Duration: " (:duration @state)
            [slider {:min 1
                     :max 60
                     :value (:duration @state)
                     :on-change (fn [e]
                                  (swap! state assoc :duration (.. e -target -value)))}]]]

          [:button {:on-click reset-timer} "Reset"]])

       :component-did-update
       (fn [this]
         (if (>= (/ (:elapsed-time @state) 1000) (:duration @state))
           (clear-timer)
           (when (nil? @timer-handle)
             (start-timer))))

       :component-did-mount
       (fn [this]
         (start-timer))

       :component-will-unmount
       (fn [this]
         (clear-timer))})))
