package net.thevpc.halfa.api;

import java.awt.Color;
import net.thevpc.halfa.api.model.HSize;
import net.thevpc.halfa.api.model.HStyle;
import net.thevpc.halfa.api.model.HStyleType;

/**
 *
 * @author vpc
 */
public class HStyles {

    public static HStyle fontSize(Double value) {
        return new HStyle(HStyleType.FONT_SIZE, value);
    }

    public static HStyle fontFamily(String value) {
        return new HStyle(HStyleType.FONT_FAMILY, value);
    }

    public static HStyle width(HSize size) {
        return new HStyle(HStyleType.WIDTH, size);
    }

    public static HStyle height(HSize size) {
        return new HStyle(HStyleType.HEIGHT, size);
    }

    public static HStyle foregroundColor(Color color) {
        return new HStyle(HStyleType.FOREGROUND_COLOR, color);
    }

    public static HStyle backgroundColor(Color color) {
        return new HStyle(HStyleType.BACKGROUND_COLOR, color);
    }

    public static HStyle fontBold(boolean value) {
        return new HStyle(HStyleType.FONT_BOLD, value);
    }

    public static HStyle fontItalic(boolean value) {
        return new HStyle(HStyleType.FONT_ITALIC, value);
    }

    public static HStyle fontUnderlined(boolean value) {
        return new HStyle(HStyleType.FONT_UNDERLINED, value);
    }
}
