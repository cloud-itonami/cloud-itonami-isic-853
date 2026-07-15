(ns highereds.advisor
  "Contained intelligence node for the higher-education actor.
  The advisor shapes proposals given a context (student, available operations,
  current phase). It never makes governance decisions -- only the governor does.")

(defn mock-advisor
  "Mock advisor for demo/testing.
  Returns a proposal shaped for the given operation and student."
  [store student-id op phase]
  {:student-id student-id
   :op op
   :effect :propose
   :created-at (java.time.Instant/now)
   :reasoning "Mock advisor proposal for demo"
   :description (case op
                  :schedule-enrollment-appointment
                  "Schedule enrollment/advising appointment for student"
                  :coordinate-facility-booking
                  "Book classroom/lab facility for student use"
                  :coordinate-supply-request
                  "Request administrative supplies"
                  :schedule-staff-shift-proposal
                  "Propose staff shift arrangement"
                  :flag-safety-concern
                  "Flag potential safety/wellbeing concern"
                  "Unknown operation")
   :metadata {:phase phase
              :confidence 0.85
              :source :mock-advisor}})

(defn advisor
  "Primary advisor entry point.
  Takes store, student-id, operation, and phase.
  Returns a proposal map with :student-id, :op, :effect, :created-at,
  :reasoning, :description, :metadata."
  [store student-id op phase]
  (mock-advisor store student-id op phase))
