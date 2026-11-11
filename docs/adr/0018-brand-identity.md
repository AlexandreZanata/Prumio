# ADR-0018: Adopt the sprout logo and Prumio Green as the brand identity

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

The owner provided the official logo (a white sprout over stacked layers on a soft green rounded
square) and asked for a high-quality vector version, documented usage, and a design system that
uses the logo's green as the main accent. The phase-0 palette used a provisional darker green.

## Decision

- The logo is vectorized from the original artwork (traced path, 99.5 % pixel overlap) and stored
  with its regeneration scripts; every asset (SVGs, PNGs, Android launcher icon, in-app vector) is
  generated from that single path (docs/brand/README.md).
- **Prumio Green `#88C29A`** is the brand color and the only accent hue. The color tokens are a
  tonal scale of it: the green itself is a surface color (FAB, brand surfaces, `primaryContainer`
  in light, `primary` in dark); a deeper tone `#2E6E47` carries text and icons in the light theme.
- In-app, the logo is shown only through `PrumioLogo`.
- WCAG contrast of all text tokens is enforced by `PrumioColorsContrastTest`.

## Consequences

- Consistent identity across launcher, app and docs; one place to change it.
- White text on the brand green is forbidden (2.05:1); documented and covered by the token design.

## Alternatives considered

- **Use `#88C29A` as `primary` in the light theme** — fails contrast for text and filled buttons.
- **Keep the raster logo** — blurry at large sizes, unusable for adaptive icons.
