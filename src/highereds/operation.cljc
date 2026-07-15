(ns highereds.operation
  "Closed proposal-operation allowlist for the higher-education actor.

  Higher-education students are typically legal adults, but the actor
  remains coordination-only: enrollment/advising appointments, facility
  booking, administrative supply coordination, staff shift proposals,
  and safety-concern escalation. Never academic judgment, admissions
  decisions, academic standing determinations, degree conferral, or
  disciplinary action.")

(defn valid-operation?
  "Returns true if op is in the closed allowlist."
  [op]
  (#{:schedule-enrollment-appointment
     :coordinate-facility-booking
     :coordinate-supply-request
     :schedule-staff-shift-proposal
     :flag-safety-concern}
   op))

(defn all-operations
  "Closed allowlist of operations."
  []
  [:schedule-enrollment-appointment
   :coordinate-facility-booking
   :coordinate-supply-request
   :schedule-staff-shift-proposal
   :flag-safety-concern])

(defn operation-description
  "Human-readable description of each operation."
  [op]
  (case op
    :schedule-enrollment-appointment "Schedule enrollment/advising appointment"
    :coordinate-facility-booking "Coordinate facility/classroom booking"
    :coordinate-supply-request "Coordinate administrative supply request"
    :schedule-staff-shift-proposal "Propose staff shift/coverage arrangement"
    :flag-safety-concern "Escalate safety/wellbeing concern"
    "Unknown operation"))
