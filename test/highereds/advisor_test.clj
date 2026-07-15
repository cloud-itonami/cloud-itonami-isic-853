(ns highereds.advisor-test
  (:require [clojure.test :as t]
            [highereds.store :as store]
            [highereds.advisor :as advisor]))

(t/deftest mock-advisor-enrollment-proposal
  (let [s (store/seed-db)
        proposal (advisor/advisor s "student-1" :schedule-enrollment-appointment :phase-1)]
    (t/is (= "student-1" (:student-id proposal)))
    (t/is (= :schedule-enrollment-appointment (:op proposal)))
    (t/is (= :propose (:effect proposal)))
    (t/is (string? (:description proposal)))
    (t/is (not (nil? (:created-at proposal))))))

(t/deftest mock-advisor-facility-proposal
  (let [s (store/seed-db)
        proposal (advisor/advisor s "student-2" :coordinate-facility-booking :phase-2)]
    (t/is (= "student-2" (:student-id proposal)))
    (t/is (= :coordinate-facility-booking (:op proposal)))
    (t/is (= :propose (:effect proposal)))))

(t/deftest mock-advisor-supply-proposal
  (let [s (store/seed-db)
        proposal (advisor/advisor s "student-1" :coordinate-supply-request :phase-2)]
    (t/is (= :coordinate-supply-request (:op proposal)))
    (t/is (string? (:description proposal)))))

(t/deftest mock-advisor-shift-proposal
  (let [s (store/seed-db)
        proposal (advisor/advisor s "student-3" :schedule-staff-shift-proposal :phase-2)]
    (t/is (= :schedule-staff-shift-proposal (:op proposal)))
    (t/is (string? (:description proposal)))))

(t/deftest mock-advisor-safety-proposal
  (let [s (store/seed-db)
        proposal (advisor/advisor s "student-1" :flag-safety-concern :phase-3)]
    (t/is (= :flag-safety-concern (:op proposal)))
    (t/is (= :propose (:effect proposal)))
    (t/is (string? (:description proposal)))))

(t/deftest advisor-proposal-structure
  (let [s (store/seed-db)
        proposal (advisor/advisor s "student-1" :coordinate-supply-request :phase-2)]
    (t/is (contains? proposal :student-id))
    (t/is (contains? proposal :op))
    (t/is (contains? proposal :effect))
    (t/is (contains? proposal :created-at))
    (t/is (contains? proposal :description))
    (t/is (contains? proposal :reasoning))
    (t/is (contains? proposal :metadata))))

(t/deftest advisor-metadata
  (let [s (store/seed-db)
        proposal (advisor/advisor s "student-1" :schedule-enrollment-appointment :phase-1)
        meta (:metadata proposal)]
    (t/is (= :phase-1 (:phase meta)))
    (t/is (number? (:confidence meta)))
    (t/is (= :mock-advisor (:source meta)))))
