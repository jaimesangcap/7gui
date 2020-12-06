(ns sevengui.flight-booker-test
  (:require
    [cljs.test :refer [deftest testing is]]
    ["@testing-library/react" :as rtl]
    [reagent.core :as r]
    [sevengui.test-utils :refer [with-component]]
    [sevengui.flight-booker :refer [flight-booker]]))

(deftest flight-booker-component
  (testing "should render flight-booker in correct state"
    (let [default-date-string "2021-02-17"]
      (with-component
        [flight-booker {:default-date default-date-string}]
        (fn [view]
          (let [start-date-input (.getByRole view "textbox" #js {:name "start-date"})
                return-date-input (.getByRole view "textbox" #js {:name "return-date"})
                button (.getByRole view "button")
                options (.getAllByRole view "option")]
            (is (= default-date-string (.-value start-date-input)))
            (is (= default-date-string (.-value return-date-input)))
            (is (= 2 (count options)))
            (is (= "one-way" (.-value (first options))))
            (is (= true (.-selected (first options))))
            (is (= "return" (.-value (nth options 1))))
            (is (= true (.-disabled return-date-input)))
            (is (= false (.-disabled button))))))))

  (testing "should display a message when book button has been clicked for one-way flight"
    (with-component
      [flight-booker]
      (fn [view]
        (let [start-date-input (.getByRole view "textbox" #js {:name "start-date"})
              button (.getByRole view "button")]
          (.change rtl/fireEvent start-date-input (clj->js {:target {:value "2020-12-24"}}))
          (r/flush)
          (.click rtl/fireEvent button)
          (r/flush)
          (.getByText view "You have booked a one-way flight on 2020-12-24")))))

  (testing "should display a message when book button has been clicked for return flight"
    (with-component
      [flight-booker]
      (fn [view]
        (let [select (.getByRole view "combobox")
              start-date-input (.getByRole view "textbox" #js {:name "start-date"})
              return-date-input (.getByRole view "textbox" #js {:name "return-date"})
              button (.getByRole view "button")]
          (.change rtl/fireEvent select (clj->js {:target {:value "return"}}))
          (.change rtl/fireEvent start-date-input (clj->js {:target {:value "2020-12-23"}}))
          (.change rtl/fireEvent return-date-input (clj->js {:target {:value "2021-01-03"}}))
          (r/flush)
          (.click rtl/fireEvent button)
          (r/flush)
          (.getByText view "You have booked a return flight on 2020-12-23 to 2021-01-03")))))

  (testing "should enable return date textbox when return flight option is selected"
    (with-component
      [flight-booker]
      (fn [view]
        (let [select (.getByRole view "combobox")
              return-date-input (.getByRole view "textbox" #js {:name "return-date"})]
          (is (= true (.-disabled return-date-input)))
          (.change rtl/fireEvent select (clj->js {:target {:value "return"}}))
          (r/flush)
          (is (= false (.-disabled return-date-input)))
          (.change rtl/fireEvent select (clj->js {:target {:value "one-way"}}))
          (r/flush)
          (is (= true (.-disabled return-date-input)))))))

  (testing "should disable book button and change background to red when enabled date field have invalid value for one-way flight"
    (with-component
      [flight-booker]
      (fn [view]
        (let [start-date-input (.getByRole view "textbox" #js {:name "start-date"})
              button (.getByRole view "button")]
          (is (= false (.-disabled button)))
          (.change rtl/fireEvent start-date-input (clj->js {:target {:value "invalid-date"}}))
          (r/flush)
          (is (= "red" (.. start-date-input -style -backgroundColor)))
          (is (= true (.-disabled button)))
          ;; ensure proper state after changed to valid date
          (.change rtl/fireEvent start-date-input (clj->js {:target {:value "2020-12-25"}}))
          (r/flush)
          (is (= "" (.. start-date-input -style -backgroundColor)))
          (is (= false (.-disabled button)))))))

  (testing "should disable book button and change background to red when enabled date field have invalid value for return flight"
    (with-component
      [flight-booker]
      (fn [view]
        (let [select (.getByRole view "combobox")
              start-date-input (.getByRole view "textbox" #js {:name "start-date"})
              return-date-input (.getByRole view "textbox" #js {:name "return-date"})
              button (.getByRole view "button")]
          (is (= false (.-disabled button)))
          (.change rtl/fireEvent select (clj->js {:target {:value "return"}}))
          (r/flush)
          (.change rtl/fireEvent start-date-input (clj->js {:target {:value "invalid-date"}}))
          (.change rtl/fireEvent return-date-input (clj->js {:target {:value "invalid-date"}}))
          (r/flush)
          (is (= "red" (.. start-date-input -style -backgroundColor)))
          (is (= "red" (.. return-date-input -style -backgroundColor)))
          (is (= true (.-disabled button)))
          ;; ensure proper state after changing to valid date
          (.change rtl/fireEvent start-date-input (clj->js {:target {:value "2020-12-25"}}))
          (.change rtl/fireEvent return-date-input (clj->js {:target {:value "2020-12-25"}}))
          (r/flush)
          (is (= "" (.. start-date-input -style -backgroundColor)))
          (is (= "" (.. return-date-input -style -backgroundColor)))
          (is (= false (.-disabled button)))))))

  (testing "should disable book button when return-date is before the start-date"
    (with-component
      [flight-booker]
      (fn [view]
        (let [select (.getByRole view "combobox")
              start-date-input (.getByRole view "textbox" #js {:name "start-date"})
              return-date-input (.getByRole view "textbox" #js {:name "return-date"})
              button (.getByRole view "button")]
          (.change rtl/fireEvent select (clj->js {:target {:value "return"}}))
          (.change rtl/fireEvent start-date-input (clj->js {:target {:value "2020-12-04"}}))
          (.change rtl/fireEvent return-date-input (clj->js {:target {:value "2020-12-05"}}))
          (r/flush)
          (is (= false (.-disabled button)))
          (.change rtl/fireEvent return-date-input (clj->js {:target {:value "2020-12-03"}}))
          (r/flush)
          (is (= true (.-disabled button)))
          (.change rtl/fireEvent start-date-input (clj->js {:target {:value "2020-12-01"}}))
          (r/flush)
          (is (= false (.-disabled button))))))))

