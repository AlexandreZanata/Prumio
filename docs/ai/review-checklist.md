# Review checklist

Use it to review your own change before reporting, or someone else's (human or agent) before
merging. Each "no" must be fixed or explicitly justified in the report.

## Correctness

- Does the change do what the task asked — no more, no less?
- Are all business decisions in `:core:domain`, citing rule ids? Any `if` about a business concept
  in a ViewModel or composable is a smell.
- Are expected failures returned as `Outcome.Failure(DomainError)` and handled in the UI?
- Are time computations done with `TimeProvider` and the rules of time-semantics.md (floating vs
  zoned, DST)?
- Are multi-entity writes atomic (one port method, one transaction)?
- Do `Flow`s update the UI when data changes elsewhere?

## Reuse and structure

- Did you search the component catalog, use cases and shared strings before creating new ones?
- Is anything duplicated (copy-pasted composable, query, mapping, string, constant)? Move it down
  to the right core module instead.
- Is every new file in the right module and package, named per conventions.md, ≤ 400 lines?
- Are feature internals `internal`/`private`?

## Tests

- Is every new or changed rule proven by a test named after its id?
- Are edge cases covered (blank input, limits, empty lists, midnight, DST, deleted items)?
- Do tests use fakes and fixed time, without sleeps or real clocks?

## UI and UX

- Only `Prumio*`/`:core:ui` components? Tokens instead of literals?
- English + Brazilian Portuguese strings, natural wording, glossary terms?
- Empty, loading-free, long-text and error states considered?
- TalkBack labels name action and item; touch targets ≥ 48 dp; status not color-only?
- Calm tone: no guilt, no pressure, no unnecessary notifications (ux-principles.md)?

## Privacy and safety

- No network, analytics, logging of user content or new permissions without approval?
- Destructive actions confirmed and undoable?

## Docs

- Rule statuses, component catalog, contracts, model, schema, module README, plan status and
  CHANGELOG updated as needed?
- Would the next agent understand this change from the docs alone?
