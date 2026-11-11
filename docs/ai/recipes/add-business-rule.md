# Recipe: add or change a business rule

Use when behavior changes: a new rule, a different limit, a new edge case.

## Steps

1. **Check for an existing rule** in [business-rules.md](../../domain/business-rules.md) (search the
   area and keywords). Prefer refining an existing rule over adding an overlapping one.
2. **Write the rule** under its area with the next free number (never reuse a number):

   ```markdown
   ### BR-TASK-010 — Short imperative title
   - **Status:** planned · **Phase:** <phase>
   - **Rule:** Precise, testable statement. Include limits, ordering, defaults, edge cases.
   - **Why:** (optional) the product reason.
   ```
   New area? Add it to the "Areas" line at the top and to the glossary if it introduces terms.
3. **Ask the owner** if the rule changes existing user-visible behavior or contradicts another rule.
4. **Write the failing test first**, in the lowest layer that can prove it (usually `:core:domain`),
   named ``fun `BR-TASK-010 <what is proven>`()``.
5. **Implement** in the domain (use case or policy). Cite the id in KDoc: `Rule: BR-TASK-010`.
6. **Flip the status** to `implemented` once the test passes.
7. If UI copy explains the rule, update strings (both languages) and docs/product/screens.md.

## Changing a rule

Edit its text in place (the id stays), update the tests that cite it, and mention the id in the
commit message: `feat(tasks)!: postpone keeps reminders (BR-TASK-007)`.

## Validation

```bash
./gradlew :core:domain:testAndroidHostTest
./gradlew :tooling:architecture-tests:test   # traceability: ids exist; implemented ⇒ tested
```
