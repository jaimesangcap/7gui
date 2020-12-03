(ns sevengui.test-utils
  (:require
    ["@testing-library/react" :as rtl]
    [reagent.core :as r]))

(defn with-component [component body]
  ;; Adding explicit container will prevent browser-test runners elements getting mixed-up with the component elements
  (let [container (.appendChild (.-body js/document) (js/document.createElement "div"))
        view (rtl/render
               (r/as-element component)
               #js {:container container
                    :baseElement container})]
    (try
      (body view)
      (finally
        (.remove container)
        (rtl/cleanup)))))
