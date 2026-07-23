# Business Model: Higher Education (Administrative Coordination)

## Classification
- Repository: `cloud-itonami-isic-853`
- ISIC Rev.4: `853` — higher education, narrowed to back-office
  administrative COORDINATION (never grading, curriculum, admissions,
  academic-standing, degree conferral, discipline, or custody)
- Social impact: student-autonomy administrative-coordination
  institutional-operations equitable-access

## Customer

- university/college registrar, advising and facilities-office
  coordinators who cannot justify a full enterprise student-success
  platform (Starfish/EAB Navigate/Symplicity) for a single
  department or a small institution
- small colleges and community colleges priced out of institution-wide
  enterprise contracts

## Problem

Enrollment/advising appointment scheduling, classroom/facility booking,
supply coordination, staff shift proposals and wellbeing-concern flagging
are typically run on paper, spreadsheets or a generic calendar tool, with
no immutable record of who decided what and no structural guarantee that a
wellbeing/mental-health-risk signal reaches a human rather than being
silently logged. Enterprise student-success platforms (Starfish, EAB
Navigate, Symplicity) exist but are institution-wide, sales-gated with no
public pricing, and pitched at a scale a single department or small college
often can't justify.

## Offer

- `schedule-enrollment-appointment`, `coordinate-facility-booking`,
  `coordinate-supply-request`, `schedule-staff-shift-proposal`,
  `flag-safety-concern` (wellbeing/mental-health-risk signals; always
  escalates, never auto-commits at any phase)
- an independent Governor with three permanent HARD checks (student
  registered/verified; every effect must be `:propose`; grading/academic-
  assessment/curriculum/admissions/academic-standing/degree-conferral/
  discipline/custody/safety-authority content permanently blocked)
- an append-only audit ledger

## Funnel (demo → fork → certified operator)

1. **Demo** — the nightly build-time-regenerated operator console.
2. **Fork / self-host** — AGPL-3.0-or-later; run the actor for one
   department or institution.
3. **itonami.cloud certification** (optional) — same trust ladder as every
   cloud-itonami venture.

## Revenue

| Package | Customer | Price shape (example) |
|---|---|---|
| Self-host starter | registrar/advising-office IT lead | setup ¥100k–250k + optional support |
| Managed Advising Ops (Starter) | one department/small college, unlimited staff seats | ¥20,000/月 flat |

**Market-anchored (2026-07-23)**: benchmarked against real higher-ed
advising/scheduling comparables. The enterprise tier (Starfish/EAB Navigate,
Symplicity) is institution-wide and sales-gated with zero public pricing —
consistent with the sales-gated pattern found in the K-12 market too. Two
narrower scheduling-only tools DO publish pricing: **WaitWell** Starter at
$29/mo per location, and **SuperSaaS** starting at $9/mo — both far below
K-12's SchoolCues/Bloomz anchors. At the opposite end, **Slate** (a
full admissions CRM, not a coordination tool) starts at $30,000/year for
smaller institutions — a different product category entirely, cited here
only to show how wide this market's price spread really is. Our actor's
scope sits above the bare-scheduling tools (governed audit ledger, supply
and staffing coordination, permanent safety/scope-exclusion blocks none of
them ship) but nowhere near enterprise-CRM territory. ¥20,000/月 lands just
above WaitWell/SuperSaaS's converted range (~¥1,350–4,350/月) to reflect
that added governance value, and is **deliberately NOT** the
¥50,000–150,000/月 portfolio-uniform range used by the HR/CRM/recruiting
flagships (6399/6310/7810/5820) — that range has no evidenced relationship
to higher-ed advising-office coordination pricing, and this market's own
real comps price meaningfully lower than K-12's.

**Subscribe**: a live Stripe Payment Link for the Managed Advising Ops
(Starter) tier is pending — blocked this session on a 1Password CLI
re-authentication needed to create the live Stripe object, not yet
available. This section will be updated with the real link once created.
**No department or institution has claimed or subscribed to this tier.**

## Unit Economics (worked example, illustrative)

Same shape as `cloud-itonami-isic-851`: infrastructure ≈ ¥3k–8k/月, LLM cost
bounded to proposal-time only, human approval labor is the real cost driver
(~2–4 h/月 once the rollout phase stabilizes), support ~2–3 h/月. Business
scales with number of departments/institutions per operator, not proposal
volume.

## Open Participation

Anyone may fork, run the demo, self-host, submit patches, and build a local
operator business. itonami.cloud certification is required before an
operator is listed, receives leads, or runs managed tenants under the
platform brand.

## Operator Trust Levels

| Level | Capability |
|---|---|
| Contributor | patches, docs, examples |
| Self-host operator | runs their own department/institution's instance, no platform endorsement |
| Certified operator | listed on itonami.cloud after review |
| Managed operator | may receive leads and operate customer tenants |
| Core maintainer | can approve changes to governor, security and governance |

## Trust Controls

- a proposal for an unregistered/unverified student is never committed or
  even escalated
- any proposal whose effect is not `:propose`, or whose content touches
  grading/academic-assessment/curriculum/admissions/academic-standing/
  degree-conferral/discipline/custody/safety-authority territory, is
  permanently blocked
- a safety-concern flag always reaches a human; it can never auto-commit
- every commit, hold, escalation and approval path is auditable
- sensitive student data stays outside Git

## Non-Negotiables

- Do not commit real student PII to this repository.
- Do not bypass the Governor for production commits.
- Do not expand the proposal-op allowlist to grading, academic assessment,
  curriculum, admissions, academic-standing, degree conferral, discipline,
  or custody decisions without a new ADR.
- Do not market an uncertified deployment as an itonami.cloud certified
  operator.
