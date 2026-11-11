# Recipe: add a user-facing string

1. **Search first**: `rg -i "<english text>" --glob "*.xml" core feature app`. Generic actions,
   destination names and model labels already exist in `:core:ui`.
2. **Choose the location** ([i18n.md](../../architecture/i18n.md#where-strings-live)): used by more
   than one module → `core/ui/.../composeResources`; otherwise the feature's own `composeResources`.
3. **Add to both languages, same key, same commit**:
   - `.../composeResources/values/strings.xml` — English
   - `.../composeResources/values-pt-rBR/strings.xml` — Brazilian Portuguese (natural phrasing; use
     glossary terms: Pendências, Entrada, Aguardando…)
4. Key name: `<feature>_<screen?>_<meaning>` (shared: `action_*`, `destination_*`, `<model>_<value>`).
   Counts use `<plurals>`; variables use `%1$s` / `%1$d`.
5. Use it: `stringResource(Res.string.key)` (feature `Res` is `<feature package>.resources.Res`;
   shared: `import app.prumio.core.ui.resources.Res as CoreUiRes`).
6. A string moved from a feature to `:core:ui`? Delete the feature copy in the same change.

Validation: build the module (`./gradlew :feature:<x>:assemble`) — accessors are generated at build time.
