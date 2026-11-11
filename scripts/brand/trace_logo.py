# Traces the white mark of docs/brand/source/prumio-logo-original.webp into SVG path data.
# Needs the pure-Python potrace port: python3 -m venv .venv && .venv/bin/pip install potracer pillow numpy
# Usage: .venv/bin/python scripts/brand/trace_logo.py docs/brand/source/prumio-logo-original.webp docs/brand/source/prumio-mark-path.txt 2 0.6
import sys
import numpy as np
from PIL import Image
import potrace

SRC = sys.argv[1]
OUT = sys.argv[2]
SCALE = int(sys.argv[3])
X0, Y0, X1, Y1 = 260, 160, 1000, 1160  # region inside the green square, contains the whole mark

img = Image.open(SRC).convert("RGB").crop((X0, Y0, X1, Y1))
from PIL import ImageFilter
red = img.getchannel("R").filter(ImageFilter.GaussianBlur(1.2)).resize(((X1 - X0) * SCALE, (Y1 - Y0) * SCALE), Image.BICUBIC)
mask = np.array(red) > 196  # white mark vs green (#88C29A, R=136)

bmp = potrace.Bitmap(~mask)  # potracer traces False pixels
path = bmp.trace(turdsize=200, turnpolicy=potrace.POTRACE_TURNPOLICY_MINORITY, alphamax=1.0, opticurve=True, opttolerance=float(sys.argv[4]))

def pt(p):
    return f"{X0 + p.x / SCALE:.2f},{Y0 + p.y / SCALE:.2f}"

parts = []
for curve in path:
    parts.append("M" + pt(curve.start_point))
    for seg in curve.segments:
        if seg.is_corner:
            parts.append("L" + pt(seg.c) + "L" + pt(seg.end_point))
        else:
            parts.append("C" + pt(seg.c1) + " " + pt(seg.c2) + " " + pt(seg.end_point))
    parts.append("Z")
d = "".join(parts)
open(OUT, "w").write(d)
print("curves:", len(path), "chars:", len(d))
