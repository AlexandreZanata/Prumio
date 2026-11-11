# Accessibility

Prumio must be fully usable with TalkBack, large fonts and high contrast. Requirements apply to
every screen; the full audit happens in phase 6 (P6.11) but nothing new may regress them.

## Requirements

| Area | Requirement |
|---|---|
| Touch targets | ≥ 48 × 48 dp (`PrumioSpacing.minTouchTarget`); design-system buttons already comply |
| Labels | Every actionable element has a label naming action **and** object: "Complete task Pay internet bill" |
| Icons | Meaningful icons have `contentDescription`; decorative ones pass `null` |
| Status | Never conveyed by color alone (status chips have text; overdue has text) |
| Text scaling | Layouts work at 200 % font scale: no clipped text, lists reflow, no fixed heights for text |
| Contrast | Text ≥ 4.5:1, large text and UI components ≥ 3:1, in light and dark themes |
| Focus order | Logical reading order; dialogs trap focus; Quick capture focuses the text field |
| Headings | Section headers are marked as headings (`semantics { heading() }`) — `PrumioSectionHeader` (P2.09) |
| Motion | Respect "remove animations"; no essential information in animation |
| Time limits | Undo snackbars last ≥ 5 s and are announced; nothing important disappears on a timer |

## How it is checked

- Design-system components encode the defaults (touch targets, labels as required parameters).
- Phase 2 adds screenshot tests at 200 % font scale and in dark theme for every component (P2.09).
- Phase 6 runs a manual TalkBack pass over every screen (docs/plan/qa/device-qa-checklist.md) and
  fixes findings (P6.11).
- Reviewers use docs/ai/review-checklist.md, which includes accessibility items.
