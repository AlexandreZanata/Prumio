# Changelog

All notable changes to Prumio are documented here. Format: [Keep a Changelog](https://keepachangelog.com/en/1.1.0/);
versions follow [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added
- Phase 0 foundation: Kotlin Multiplatform (Android-only) project with 20 modules, convention
  plugins, enforced module dependency rules, version catalog.
- App shell: bottom navigation (Today, Agenda, Matters, Inbox), "Add anything" dialog placeholder,
  Search and Settings placeholders, light/dark theme, English and Brazilian Portuguese strings.
- Reference slice: Today greeting and localized date (BR-TODAY-001).
- Quality gates: ktlint + Compose rules, Konsist architecture tests, business-rule traceability,
  privacy gate (no INTERNET permission), SQLDelight migration verification, `scripts/verify.sh`, CI.
- Documentation: product vision, domain model and business rules, architecture, design system,
  AI agent manual and recipes, execution plan with validation gates, ADR-0001 … ADR-0017.
