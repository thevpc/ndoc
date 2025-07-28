package net.thevpc.ndoc.api.eval;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class NDocFontBySizeResolver {
    public static final NDocFontBySizeResolver INSTANCE=new NDocFontBySizeResolver();

    private Map<Key, Font> cache = new HashMap<>();

    private static class Key {
        String name;
        int style;
        int sizeMul100;

        public Key(String name,int style, int sizeMul100) {
            this.name = name;
            this.style = style;
            this.sizeMul100 = sizeMul100;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return sizeMul100 == key.sizeMul100
             && style == key.style
                    && Objects.equals(name, key.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name,style, sizeMul100);
        }
    }

    public Font getFont(String name, int style, double height, Function<Font, FontMetrics> fontMetricsFunction) {
        Key k = new Key(name, style,(int) (height * 100));
        return cache.computeIfAbsent(k, r -> getFont0(name, style, height, fontMetricsFunction));
    }

    public Font getFont0(String name, int style, double dheight, Function<Font, FontMetrics> fontMetricsFunction) {
        int height = (int) dheight;
        int size = height;
        Boolean up = null;
        while (true) {
            Font font = new Font(name, style, size);
            FontMetrics fm = fontMetricsFunction.apply(font);
            int testHeight = fm.getHeight() + fm.getAscent() + fm.getDescent();
            if (testHeight < height && up != Boolean.FALSE) {
                size++;
                up = Boolean.TRUE;
            } else if (testHeight > height && up != Boolean.TRUE) {
                size--;
                up = Boolean.FALSE;
            } else {
                return font;
            }
        }
    }
}
