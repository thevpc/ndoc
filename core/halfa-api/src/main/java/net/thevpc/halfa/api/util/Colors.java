package net.thevpc.halfa.api.util;

import java.awt.*;

public class Colors {
    public static float[] hsb(Color c) {
        return Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
    }

    public static Color withH(Color c, float h) {
        float[] o = hsb(c);
        return new Color(Color.HSBtoRGB(h, o[1], o[2]));
    }

    public static Color withS(Color c, float s) {
        float[] o = hsb(c);
        return new Color(Color.HSBtoRGB(o[0], s, o[2]));
    }

    public static Color withB(Color c, float b) {
        float[] o = hsb(c);
        return new Color(Color.HSBtoRGB(o[0], o[1], b));
    }
}
