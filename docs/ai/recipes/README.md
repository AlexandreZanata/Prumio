# Recipes

One recipe per kind of change. Each lists the exact files to touch, a template and the validation
commands. Follow them literally; if a recipe is wrong or incomplete, fix the recipe in the same change.

| I need to… | Recipe |
|---|---|
| Add a business rule or change one | [add-business-rule.md](add-business-rule.md) |
| Add a use case (business operation) | [add-use-case.md](add-use-case.md) |
| Add a repository / domain port and its implementation | [add-repository.md](add-repository.md) |
| Add or change a table, column or index | [add-database-table.md](add-database-table.md) |
| Add a screen to an existing feature | [add-screen.md](add-screen.md) |
| Add a new feature module | [add-feature-module.md](add-feature-module.md) |
| Add a navigation destination | [add-navigation-key.md](add-navigation-key.md) |
| Add a UI component (generic or domain-aware) | [add-design-system-component.md](add-design-system-component.md) |
| Add a user-facing string | [add-string.md](add-string.md) |
| Add a user setting | [add-setting.md](add-setting.md) |
| Add a kind of notification | [add-notification-type.md](add-notification-type.md) |
| Add a third-party library | [add-library.md](add-library.md) |

All recipes end with: `./gradlew spotlessApply && ./scripts/verify.sh`, then the
[definition of done](../definition-of-done.md).
