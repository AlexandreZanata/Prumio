# UX principles

Rules every screen follows. Reviewers (human or AI) check new UI against this list.

## Effort

1. **A title is enough.** No form requires more than one field to save (BR-GEN-001).
2. **Defaults over questions.** Prefer a sensible default plus an easy edit to asking upfront.
3. **One primary action per screen**, visually distinct (`PrumioButtonVariant.Primary`).
4. **Capture in two taps**: Add → type → Save (BR-INBOX-002). Keyboard opens immediately.
5. **Quick choices first**: chips for Today / Tomorrow / Next week before a full date picker;
   presets ("1 hour before") before custom values.

## Calm

6. **No guilt.** No streaks, scores, percentages, red badges for productivity, or "you failed" copy
   (BR-REVIEW-002). Overdue is stated plainly ("Overdue since Mon"), not dramatized.
7. **Notifications are rare and useful.** Only reminders the user asked for, the summary they enabled
   and follow-ups they set. Never marketing, tips or "we miss you".
8. **Empty states are kind and helpful**: say what will appear and how to add it.

## Trust

9. **Nothing disappears silently.** Ignoring something never changes it (BR-GEN-006). Destructive
   actions need explicit intent and offer Undo (BR-GEN-005).
10. **Explain limits honestly**: when the device may delay reminders, say so and show how to fix it
    (BR-REM-013, BR-REM-014).
11. **Ask for permissions in context**, after a one-sentence reason, never at first launch.
12. **Privacy is visible**: the About screen states that Prumio has no internet access.

## Consistency

13. **Same thing, same look, everywhere**: a task always renders with `TaskRow`, a matter with
    `MatterCard` (docs/design-system/components.md). Never two visual versions of one concept.
14. **Same words everywhere**: use [glossary.md](../domain/glossary.md) terms in both languages.
15. **Status is never color-only**: chips have text; icons have content descriptions.

## Accessibility

16. Touch targets ≥ 48 dp; text scales to 200 % without truncating meaning; contrast ≥ 4.5:1 for text.
17. Every actionable element has a TalkBack label that names the action and the item
    ("Complete task Pay internet bill").
18. Motion is subtle and respects the system "remove animations" setting.

Details: [architecture/accessibility.md](../architecture/accessibility.md).

## Writing style (UI copy)

- Short, plain, friendly; second person; sentence case ("Add next action", not "Add Next Action").
- Times and dates follow the device locale (24 h vs 12 h).
- pt-BR copy is written natively, not translated word by word ("Pendências", "Aguardando",
  "Seu dia, em resumo").
