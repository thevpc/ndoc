package net.thevpc.ndoc.spi.util;

import net.thevpc.nuts.util.NAssert;

import java.awt.*;
import java.util.Arrays;

public class DefaultColorPalette implements ColorPalette {
    public static final ColorPalette INSTANCE = new DefaultColorPalette(0x26355D, 0xAF47D2, 0xFF8F00, 0xFFDB00);
    private Color[] all;

    public DefaultColorPalette(int... all) {
        NAssert.requireTrue(all != null && all.length > 0, "color array");
        this.all = Arrays.stream(all).mapToObj(i -> new Color(i)).toArray(Color[]::new);
    }

    public DefaultColorPalette(Color... all) {
        NAssert.requireTrue(all != null && all.length > 0, "color array");
        this.all = all;
    }

    @Override
    public Color getColor(double index) {
        index = Math.abs(index);
        if (!Double.isFinite(index)
                || index <= 1
        ) {
            return all[0];
        }
        int ii = (int) index;
        if (ii <= 1) {
            return all[0];
        }
        int i = Math.abs((ii - 1) % all.length);
        return all[i];
    }
}
