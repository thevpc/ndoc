package net.thevpc.ndoc.engine.eval.fct;

import java.awt.*;

public class NDocColorUtils {
    public static float[] rgbToHsl(Color color) {
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float h, s, l = (max + min) / 2f;

        if (max == min) {
            h = s = 0f;
        } else {
            float d = max - min;
            s = l > 0.5f ? d / (2f - max - min) : d / (max + min);
            if (max == r) h = (g - b) / d + (g < b ? 6f : 0f);
            else if (max == g) h = (b - r) / d + 2f;
            else h = (r - g) / d + 4f;
            h /= 6f;
        }
        return new float[]{h * 360f, s, l};
    }

    public static Color hslToColor(float h, float s, float l) {
        h /= 360f;
        float r, g, b;

        if (s == 0f) {
            r = g = b = l;
        } else {
            float q = l < 0.5f ? l * (1f + s) : l + s - l * s;
            float p = 2f * l - q;
            r = hueToRgb(p, q, h + 1f / 3f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1f / 3f);
        }

        return new Color(clamp(r), clamp(g), clamp(b));
    }

    private static float hueToRgb(float p, float q, float t) {
        if (t < 0f) t += 1f;
        if (t > 1f) t -= 1f;
        if (t < 1f / 6f) return p + (q - p) * 6f * t;
        if (t < 1f / 2f) return q;
        if (t < 2f / 3f) return p + (q - p) * (2f / 3f - t) * 6f;
        return p;
    }

    private static float clamp(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    // ========== Color Operations ==========

    public static Color invert(Color color) {
        return new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
    }

    public static Color complementary(Color color) {
        float[] hsl = rgbToHsl(color);
        hsl[0] = (hsl[0] + 180f) % 360f;
        return hslToColor(hsl[0], hsl[1], hsl[2]);
    }

    public static Color grayscale(Color color) {
        int gray = (int) (0.3 * color.getRed() + 0.59 * color.getGreen() + 0.11 * color.getBlue());
        return new Color(gray, gray, gray);
    }

    public static Color adjustBrightness(Color color, float factor) {
        float[] hsl = rgbToHsl(color);
        hsl[2] = clamp(hsl[2] * factor);
        return hslToColor(hsl[0], hsl[1], hsl[2]);
    }

    public static Color adjustSaturation(Color color, float factor) {
        float[] hsl = rgbToHsl(color);
        hsl[1] = clamp(hsl[1] * factor);
        return hslToColor(hsl[0], hsl[1], hsl[2]);
    }

    public static Color rotateHue(Color color, float degrees) {
        float[] hsl = rgbToHsl(color);
        hsl[0] = (hsl[0] + degrees + 360f) % 360f;
        return hslToColor(hsl[0], hsl[1], hsl[2]);
    }

    public static Color[] analogous(Color color, float offset) {
        float[] hsl = rgbToHsl(color);
        Color c1 = hslToColor((hsl[0] + offset) % 360f, hsl[1], hsl[2]);
        Color c2 = hslToColor((hsl[0] - offset + 360f) % 360f, hsl[1], hsl[2]);
        return new Color[]{c1, c2};
    }

    public static Color[] splitComplementary(Color color, float splitAngle) {
        float[] hsl = rgbToHsl(color);
        Color c1 = hslToColor((hsl[0] + 180f - splitAngle + 360f) % 360f, hsl[1], hsl[2]);
        Color c2 = hslToColor((hsl[0] + 180f + splitAngle) % 360f, hsl[1], hsl[2]);
        return new Color[]{c1, c2};
    }

    public static Color[] triadic(Color color) {
        float[] hsl = rgbToHsl(color);
        Color c1 = hslToColor((hsl[0] + 120f) % 360f, hsl[1], hsl[2]);
        Color c2 = hslToColor((hsl[0] + 240f) % 360f, hsl[1], hsl[2]);
        return new Color[]{c1, c2};
    }

    public static Color[] monochromaticVariants(Color color, float[] lightnessFactors) {
        float[] hsl = rgbToHsl(color);
        Color[] result = new Color[lightnessFactors.length];
        for (int i = 0; i < lightnessFactors.length; i++) {
            float newL = clamp(hsl[2] * lightnessFactors[i]);
            result[i] = hslToColor(hsl[0], hsl[1], newL);
        }
        return result;
    }
}
