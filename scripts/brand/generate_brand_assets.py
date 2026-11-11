# Regenerates every brand asset from docs/brand/source/prumio-mark-path.txt.
# Usage: python3 scripts/brand/generate_brand_assets.py   (then export PNGs, see docs/brand/README.md)
import re, os, sys
REPO = sys.argv[1] if len(sys.argv) > 1 else os.path.join(os.path.dirname(__file__), "..", "..")
raw = open(os.path.join(REPO, "docs/brand/source/prumio-mark-path.txt")).read()
nums = re.compile(r"-?\d+(?:\.\d+)?")
pairs = [tuple(map(float, p.split(","))) for p in re.findall(r"-?\d+\.?\d*,-?\d+\.?\d*", raw)]
xs, ys = [p[0] for p in pairs], [p[1] for p in pairs]
minx, maxx, miny, maxy = min(xs), max(xs), min(ys), max(ys)
print("bbox", minx, miny, maxx, maxy)

def transform(scale, dx, dy, nd=2):
    def repl_pair(m):
        x, y = map(float, m.group(0).split(","))
        return f"{round(x*scale+dx, nd):g},{round(y*scale+dy, nd):g}"
    return re.sub(r"-?\d+\.?\d*,-?\d+\.?\d*", repl_pair, raw)

BRAND = "#88C29A"
# 1024 canvas, same composition as the original artwork
k = 1024 / 1254
d1024 = transform(k, 0, 0)
logo_dir = os.path.join(REPO, "docs/brand/logo")
os.makedirs(logo_dir, exist_ok=True)
header = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" width="1024" height="1024">\n  <title>Prumio</title>\n'
open(f"{logo_dir}/prumio-logo.svg", "w").write(
    header + f'  <rect width="1024" height="1024" fill="{BRAND}"/>\n  <path fill="#FFFFFF" fill-rule="evenodd" d="{d1024}"/>\n</svg>\n')
open(f"{logo_dir}/prumio-logo-rounded.svg", "w").write(
    header + f'  <rect width="1024" height="1024" rx="230" fill="{BRAND}"/>\n  <path fill="#FFFFFF" fill-rule="evenodd" d="{d1024}"/>\n</svg>\n')
# mark only, tightly framed (8% padding)
w, h = (maxx-minx), (maxy-miny)
side = max(w, h) * 1.16
km = 1024 / side
dm = transform(km, (1024 - w*km)/2 - minx*km, (1024 - h*km)/2 - miny*km)
for name, color in (("prumio-mark.svg", BRAND), ("prumio-mark-white.svg", "#FFFFFF"), ("prumio-mark-dark.svg", "#0C2715")):
    open(f"{logo_dir}/{name}", "w").write(header + f'  <path fill="{color}" fill-rule="evenodd" d="{dm}"/>\n</svg>\n')

# Android adaptive icon foreground: 108dp canvas, mark height 52dp centered in the 66dp safe zone
ka = 52 / h
da = transform(ka, 54 - (minx + w/2)*ka, 54 - (miny + h/2)*ka, nd=3)
def vector(path, size, viewport, color, comment):
    return f'''<?xml version="1.0" encoding="utf-8"?>
<!-- {comment}
     Generated from docs/brand/logo/prumio-mark.svg. Do not edit by hand: see docs/brand/README.md. -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="{size}dp"
    android:height="{size}dp"
    android:viewportWidth="{viewport}"
    android:viewportHeight="{viewport}">
    <path
        android:fillColor="{color}"
        android:fillType="evenOdd"
        android:pathData="{path}" />
</vector>
'''
open(os.path.join(REPO, "app/android/src/main/res/drawable/ic_launcher_foreground.xml"), "w").write(
    vector(da, 108, 108, "#FFFFFFFF", "Prumio mark (sprout over stacked layers), centered in the 66dp safe zone of the adaptive icon."))
# Compose resource for in-app use: mark in 24-unit viewport padding-free (tinted at runtime)
kc = 24 / max(w, h)
dc = transform(kc, (24 - w*kc)/2 - minx*kc, (24 - h*kc)/2 - miny*kc, nd=3)
cr_dir = os.path.join(REPO, "core/designsystem/src/commonMain/composeResources/drawable")
os.makedirs(cr_dir, exist_ok=True)
open(f"{cr_dir}/prumio_mark.xml", "w").write(
    vector(dc, 24, 24, "#FF000000", "Prumio mark for in-app use (PrumioLogo). Tinted at runtime."))
print("ok")
