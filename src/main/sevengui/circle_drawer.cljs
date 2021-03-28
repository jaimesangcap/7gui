(ns sevengui.circle-drawer
  (:require
   [reagent.core :as r]))

(def circle-id (r/atom 0))

(defn new-circle-id []
  (swap! circle-id inc))

(defn relative-mouse-position [e relative-elem]
  (let [rect (.getBoundingClientRect relative-elem)]
    {:x (- (.-clientX e) (.-left rect))
     :y (- (.-clientY e) (.-top rect))}))

(defn new-circle [e]
  (let [point (relative-mouse-position e (.-target e))
        circle-id (new-circle-id)]
    (assoc point
           :id circle-id
           :r 30)))

(defn undo-reducer [state action]
  (let [{:keys [past present future]} state]
    (js/console.log "reducer state before action " state)
    (condp = (:type action)
      :set {:past (conj past present)
            :present (:new-present action)
            :future []}

      :hard-set {:past past
                 :present (:new-present action)
                 :future []}

      :undo {:past (pop past)
             :present (let [v (peek past)]
                        (js/console.log "new pres " v)
                        v)
             :future (conj future present)}

      :redo {:past (conj past present)
             :present (peek future)
             :future (pop future)})))

(defn use-reducer [reducer initial-state]
  (let [state (r/atom initial-state)
        dispatch (fn [action] (swap! state reducer action))]
    [state dispatch]))

(defn use-undo [initial-present]
  (let [[state dispatch] (use-reducer undo-reducer {:past []
                                                    :present initial-present
                                                    :future []})
        actions {:set (fn [new-present] (dispatch {:type :set
                                                   :new-present new-present}))

                 ;; hard-set will not add entry to the past, meaning state can't be undo
                 :hard-set (fn [new-present] (dispatch {:type :hard-set
                                                        :new-present new-present}))
                 :undo (fn [] (dispatch {:type :undo}))
                 :redo (fn [] (dispatch {:type :redo}))}]
    [state actions]))

(defn display-coords [circle]
  (let [x (Math/floor (:x circle))
        y (Math/floor (:y circle))]
    (str "(" x ", " y ")")))

(def canvas-size {:width 600
                  :height 600})

;; TODO hide the notion of past, present, future state to the undo API user
;; TODO fix undo bug, adding entry to past when undoing
(defn circle-drawer []
  (let [[state {:keys [set hard-set undo redo]}] (use-undo {:circles {}
                                                            :selected-circle nil
                                                            :adjust-menu {:open? false
                                                                          :value 30}})
        canvas (atom nil)
        context-menu (r/atom {:x 0
                              :y 0
                              :open? false})]
    (r/create-class
     {:display-name "circle-drawer"
      :reagent-render
      (fn []
        [:div
         [:div
          [:button {:on-click #(undo)} "undo"]
          [:button {:on-click #(redo)} "redo"]]
         [:div {:style {:position "relative"
                        :width (:width canvas-size)
                        :height (:height canvas-size)}}
          [:div {:style {:display (if (:open? @context-menu) "flex" "none")
                         :position "absolute"
                         :left (:x @context-menu)
                         :top (:y @context-menu)
                         :z-index 10
                         :flex-direction "column"
                         :padding "16px 12px"
                         :background-color "white"
                         :box-shadow "0 4px 6px -1px rgba(0,0,0,0.1),0 2px 4px -1px rgba(0,0,0,0.06)"}}
           [:div {:on-click (fn [_e]
                              (swap! context-menu assoc :open? false)
                              (set (update (:present @state) :adjust-menu assoc :open? true)))}
            "Adjust Diameter"]]

          [:div {:style {:position "absolute"
                         :display (if (get-in @state [:present :adjust-menu :open?]) "block" "none")
                         :transform "translateX(-50%)"
                         :bottom "100px"
                         :left "50%"
                         :z-index 11
                         :padding "16px 12px"
                         :background-color "white"
                         :box-shadow "0 4px 6px -1px rgba(0,0,0,0.1),0 2px 4px -1px rgba(0,0,0,0.06)"}}

           [:div {:style {:display "flex" :justify-content "center" :flex-direction "column"}}
            [:div (str "Adjust diameter of circle at " (display-coords (get-in @state [:present :selected-circle])))]
            [:span "radius: " (get-in @state [:present :adjust-menu :value])]
            [:input {:type "range"
                     :default-value (get-in (:present @state) [:selected-circle :r])
                     :value (get-in @state [:present :adjust-menu :value])
                     :on-mouse-up (fn [e]
                                    (let [val (.. e -target -value)
                                          circle-id (get-in @state [:present :selected-circle :id])
                                          new-state (-> (:present @state)
                                                        (update :circles assoc-in [circle-id :r] val)
                                                        (update :adjust-menu assoc :value val))]
                                      (js/console.log "new -state " new-state)
                                      (set new-state)))
                     :on-change (fn [e]
                                  (let [val (.. e -target -value)
                                        circle-id (get-in @state [:present :selected-circle :id])
                                        new-state (-> (:present @state)
                                                      (update :circles assoc-in [circle-id :r] val)
                                                      (update :adjust-menu assoc :value val))]
                                    (hard-set new-state)))}]]]


          [:svg {:width (:width canvas-size)
                 :height (:height canvas-size)
                 :style {:border "1px solid #eee"
                         :width (str (:width canvas-size) "px")
                         :height (str (:height canvas-size) "px")}
                 :ref (fn [el] (reset! canvas el))
                 :on-click (fn [e]
                             (let [circle (new-circle e)]
                               (set (update (:present @state) :circles #(assoc % (:id circle) circle)))
                               (hard-set (assoc (:present @state) :selected-circle circle))))}
           (doall
            (for [{:keys [id x y r] :as circle} (vals (:circles (:present @state)))]
              ^{:key id}
              [:circle {:cx x
                        :cy y
                        :r r
                        :fill (if (= id (get-in (:present @state) [:selected-circle :id])) "#ccc" "white")
                        :stroke "#ccc"
                        :stroke-width 1
                        :on-click (fn [e]
                                    (.stopPropagation e)
                                    (hard-set (assoc (:present @state) :selected-circle circle)))}]))]]])


      :component-did-mount
      (fn [_this]
        (.addEventListener @canvas "contextmenu" (fn [e]
                                                   (.preventDefault e)
                                                   (let [point (relative-mouse-position e @canvas)]
                                                     (swap! context-menu assoc
                                                            :open? true
                                                            :x (:x point)
                                                            :y (:y point))))))})))

(comment
  (->> [1 2 3 4 5 6 7 8 9 19]
       (map inc)))

