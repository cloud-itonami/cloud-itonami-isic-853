(ns highereds.governor-test
  (:require [clojure.test :as t]
            [highereds.store :as store]
            [highereds.governor :as governor]))

(t/deftest valid-proposal
  (let [s (store/seed-db)
        proposal {:student-id "student-1"
                  :op :schedule-enrollment-appointment
                  :effect :propose
                  :description "Test proposal"}
        decision (governor/govern s proposal)]
    (t/is (:valid? decision))
    (t/is (empty? (:violations decision)))
    (t/is (= :commit (:action decision)))))

(t/deftest student-not-found-violation
  (let [s (store/seed-db)
        proposal {:student-id "student-999"
                  :op :coordinate-facility-booking
                  :effect :propose
                  :description "Test"}
        decision (governor/govern s proposal)]
    (t/is (not (:valid? decision)))
    (t/is (seq (:violations decision)))
    (t/is (= :hold (:action decision)))
    (t/is (some #(re-find #"not found" %) (:violations decision)))))

(t/deftest student-unverified-violation
  (let [s (store/seed-db)
        proposal {:student-id "student-3"
                  :op :coordinate-supply-request
                  :effect :propose
                  :description "Test"}
        decision (governor/govern s proposal)]
    (t/is (not (:valid? decision)))
    (t/is (seq (:violations decision)))
    (t/is (some #(re-find #"not verified" %) (:violations decision)))))

(t/deftest effect-not-propose-violation
  (let [s (store/seed-db)
        proposal {:student-id "student-1"
                  :op :schedule-staff-shift-proposal
                  :effect :commit
                  :description "Test"}
        decision (governor/govern s proposal)]
    (t/is (not (:valid? decision)))
    (t/is (seq (:violations decision)))
    (t/is (some #(re-find #"Effect must be :propose" %) (:violations decision)))))

(t/deftest scope-exclusion-grading
  (let [s (store/seed-db)
        proposal {:student-id "student-1"
                  :op :coordinate-supply-request
                  :effect :propose
                  :description "Student grading determination"}
        decision (governor/govern s proposal)]
    (t/is (not (:valid? decision)))
    (t/is (seq (:violations decision)))
    (t/is (some #(re-find #"Scope exclusion" %) (:violations decision)))))

(t/deftest scope-exclusion-admissions
  (let [s (store/seed-db)
        proposal {:student-id "student-1"
                  :op :schedule-enrollment-appointment
                  :effect :propose
                  :description "Admissions decision for candidate"}
        decision (governor/govern s proposal)]
    (t/is (not (:valid? decision)))
    (t/is (seq (:violations decision)))))

(t/deftest scope-exclusion-academic-standing
  (let [s (store/seed-db)
        proposal {:student-id "student-1"
                  :op :schedule-enrollment-appointment
                  :effect :propose
                  :description "Academic standing determination"}
        decision (governor/govern s proposal)]
    (t/is (not (:valid? decision)))
    (t/is (seq (:violations decision)))))

(t/deftest scope-exclusion-discipline
  (let [s (store/seed-db)
        proposal {:student-id "student-1"
                  :op :coordinate-supply-request
                  :effect :propose
                  :description "Disciplinary action for student"}
        decision (governor/govern s proposal)]
    (t/is (not (:valid? decision)))
    (t/is (seq (:violations decision)))))

(t/deftest safety-concern-escalates
  (let [s (store/seed-db)
        proposal {:student-id "student-1"
                  :op :flag-safety-concern
                  :effect :propose
                  :description "Mental health risk signal"}
        decision (governor/govern s proposal)]
    (t/is (:valid? decision))
    (t/is (empty? (:violations decision)))
    (t/is (= :escalate (:action decision)))))

(t/deftest safety-concern-not-self-blocked
  "Safety concern proposals should not be blocked by scope exclusion
  even if the description mentions a concern category."
  (let [s (store/seed-db)
        proposal {:student-id "student-1"
                  :op :flag-safety-concern
                  :effect :propose
                  :description "Potential CPS involvement based on signs of neglect"}
        decision (governor/govern s proposal)]
    (t/is (:valid? decision))
    (t/is (= :escalate (:action decision)))))

(t/deftest multiple-violations
  (let [s (store/seed-db)
        proposal {:student-id "student-999"
                  :op :coordinate-supply-request
                  :effect :commit
                  :description "Invalid on all counts"}
        decision (governor/govern s proposal)]
    (t/is (not (:valid? decision)))
    (t/is (>= (count (:violations decision)) 2))))
