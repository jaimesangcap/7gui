(ns sevengui.flight-booker-test
  (:require
    [cljs.test :refer [deftest testing is]]
    [sevengui.test-utils :refer [with-component]]
    [sevengui.flight-booker :refer [flight-booker]]))

(deftest flight-booker-component
  (testing "should render flight-booker in correct state"
    (let [default-date-string "2021-02-17"]
      (with-component
        [flight-booker {:default-date (js/Date. default-date-string)}]
        (fn [view]
          (let [start-date-input (.getByRole view "textbox" #js {:name "start-date"})
                return-date-input (.getByRole view "textbox" #js {:name "return-date"})
                button (.getByRole view "button")
                options (.getAllByRole view "option")]
            (is (= default-date-string (.-value start-date-input)))
            (is (= default-date-string (.-value return-date-input)))
            (is (= 2 (count options)))
            (is (= (name :one-way) (.-value (first options))))
            (is (= true (.-selected (first options))))
            (is (= (name :return) (.-value (nth options 1))))
            (is (= true (.-disabled return-date-input)))
            (is (= false (.-disabled button))))))))

  (testing "should enable return date textbox when return flight option is selected"
    (with-component
      [flight-booker]
      (fn [view]
        ()))))
