(ns highereds.governor
  "Independent compliance layer for the higher-education actor.

  Three HARD, permanent, un-overridable checks:
  1. Student verified -- target must exist AND be :registered?/:verified?
  2. Effect is :propose -- any other :effect is rejected
  3. Scope exclusion -- academic grading, admissions, academic standing,
     degree conferral, disciplinary action, safety-authority overrides
     are blocked (except legitimate :flag-safety-concern escalation)"
  (:require [clojure.string :as str]
            [highereds.store :as store]))

;; ----------------------------- Violations Detection -------------------------

(defn student-unverified-violations
  "Re-derive student verification from the store, never from self-report.
  Returns vector of violation strings, empty if verified."
  [store student-id]
  (let [student (store/student store student-id)]
    (cond
      (nil? student)
      ["Student not found in store"]

      (not (:registered? student))
      ["Student is not registered"]

      (not (:verified? student))
      ["Student is not verified"]

      :else [])))

(defn effect-must-be-propose-violations
  "Effect must be :propose; any other value is rejected."
  [proposal]
  (if (= (:effect proposal) :propose)
    []
    [(str "Effect must be :propose, got: " (:effect proposal))]))

(defn scope-exclusion-violations
  "Permanent scope boundaries. Returns vector of violation strings.
  Note: flag-safety-concern is never self-blocked; it always escalates."
  [proposal]
  ;; Never block :flag-safety-concern operations; they always escalate
  (if (= (:op proposal) :flag-safety-concern)
    []
    (let [content (str (:op proposal) " " (:description proposal))]
      (reduce
        (fn [violations pattern]
          (if (re-find (re-pattern (str "(?i)" pattern)) content)
            (conj violations (str "Scope exclusion: " pattern))
            violations))
        []
        ["grading"
         "assessment"
         "academic.?standing"
         "probation"
         "expulsion"
         "admissions?.*decision"
         "degree.*conferral"
         "disciplin"
         "safety.*authority.*override"
         "law.?enforcement"
         "CPS"
         "mandatory.*report.*override"]))))

(defn all-violations
  "Collect all violations for a proposal. Hard checks fail fast; failures
  are collected for audit."
  [store proposal]
  (let [v1 (student-unverified-violations store (:student-id proposal))
        v2 (effect-must-be-propose-violations proposal)
        v3 (scope-exclusion-violations proposal)]
    (into [] (concat v1 v2 v3))))

(defn valid-proposal?
  "Governance decision: proposal is valid iff all three hard checks pass."
  [store proposal]
  (empty? (all-violations store proposal)))

;; ----------------------------- Governor Interface -------------------------

(defrecord GovernorDecision
  [valid? violations action reasoning])

(defn govern
  "Examine a proposal against the three hard checks.
  Returns {:valid? bool :violations [] :action #{:hold :commit :escalate}
           :reasoning str}"
  [store proposal]
  (let [violations (all-violations store proposal)
        valid? (empty? violations)]
    (->GovernorDecision
      valid?
      violations
      (cond
        (not valid?) :hold
        (= (:op proposal) :flag-safety-concern) :escalate
        :else :commit)
      (if valid?
        "Proposal passed all hard checks"
        (str "Violations: " (str/join "; " violations))))))
