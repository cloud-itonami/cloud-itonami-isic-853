(ns highereds.phase-test
  (:require [clojure.test :as t]
            [highereds.phase :as phase]))

(t/deftest phase-0-read-only
  (let [ops (phase/allowed-operations :phase-0)]
    (t/is (empty? ops))
    (t/is (not (phase/operation-allowed? :phase-0 :schedule-enrollment-appointment)))))

(t/deftest phase-1-enrollment-only
  ;; :flag-safety-concern is deliberately allowed from phase-1 onward
  ;; (see phase.cljc's own fallback intent + isic-855's identical fix) --
  ;; a safety/wellbeing concern must always be raisable once the actor is
  ;; live, not gated the same way ordinary coordination ops are; it just
  ;; never auto-commits (see requires-approval? / :flag-safety-concern
  ;; always resolves to :escalate in governor/govern).
  (let [ops (phase/allowed-operations :phase-1)]
    (t/is (contains? ops :schedule-enrollment-appointment))
    (t/is (not (contains? ops :coordinate-facility-booking)))
    (t/is (contains? ops :flag-safety-concern))))

(t/deftest phase-2-multiple-ops
  (let [ops (phase/allowed-operations :phase-2)]
    (t/is (contains? ops :schedule-enrollment-appointment))
    (t/is (contains? ops :coordinate-facility-booking))
    (t/is (contains? ops :coordinate-supply-request))
    (t/is (contains? ops :schedule-staff-shift-proposal))
    (t/is (contains? ops :flag-safety-concern))))

(t/deftest phase-3-all-ops
  (let [ops (phase/allowed-operations :phase-3)]
    (t/is (contains? ops :schedule-enrollment-appointment))
    (t/is (contains? ops :coordinate-facility-booking))
    (t/is (contains? ops :coordinate-supply-request))
    (t/is (contains? ops :schedule-staff-shift-proposal))
    (t/is (contains? ops :flag-safety-concern))))

(t/deftest approval-required-phases-0-2
  (t/is (phase/requires-approval? :phase-0 :schedule-enrollment-appointment))
  (t/is (phase/requires-approval? :phase-1 :coordinate-facility-booking))
  (t/is (phase/requires-approval? :phase-2 :coordinate-supply-request)))

(t/deftest no-approval-required-phase-3
  (t/is (not (phase/requires-approval? :phase-3 :schedule-enrollment-appointment)))
  (t/is (not (phase/requires-approval? :phase-3 :flag-safety-concern))))

(t/deftest all-phases
  (let [phases (phase/all-phases)]
    (t/is (= 4 (count phases)))
    (t/is (= [:phase-0 :phase-1 :phase-2 :phase-3] phases))))

(t/deftest phase-descriptions
  (t/is (re-find #"Phase 0" (phase/phase-description :phase-0)))
  (t/is (re-find #"Phase 1" (phase/phase-description :phase-1)))
  (t/is (re-find #"Phase 2" (phase/phase-description :phase-2)))
  (t/is (re-find #"Phase 3" (phase/phase-description :phase-3))))
