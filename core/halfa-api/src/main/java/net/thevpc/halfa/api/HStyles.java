package net.thevpc.halfa.api;

import java.awt.*;
import java.awt.geom.Point2D;

import net.thevpc.halfa.api.model.*;

/**
 * @author vpc
 */
public class HStyles {

    public static HStyle fontSize(Number value) {
        return new HStyle(HStyleType.FONT_SIZE, value == null ? null : value.doubleValue());
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

    public static HStyle foregroundColor(Paint color) {
        return new HStyle(HStyleType.FOREGROUND_COLOR, color);
    }

    public static HStyle backgroundColor(Paint color) {
        return new HStyle(HStyleType.BACKGROUND_COLOR, color);
    }

    public static HStyle lineColor(Paint color) {
        return new HStyle(HStyleType.LINE_COLOR, color);
    }

    public static HStyle fontBold() {
        return fontBold(true);
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

    public static HStyle position(Point2D.Double p) {
        return new HStyle(HStyleType.POSITION, p);
    }

    public static HStyle center() {
        return anchor(HAnchor.CENTER);
    }

    public static HStyle anchor(HAnchor p) {
        return new HStyle(HStyleType.POSITION_ANCHOR, p);
    }

    public static HStyle position(double x, double y) {
        return new HStyle(HStyleType.POSITION, new Point2D.Double(x, y));
    }

    public static HStyle size(double x, double y) {
        return new HStyle(HStyleType.SIZE, new Point2D.Double(x, y));
    }

    public static HStyle size(Point2D.Double size) {
        return new HStyle(HStyleType.SIZE, size);
    }

    public static HStyle roundCorner(Point2D.Double o) {
        return new HStyle(HStyleType.ROUND_CORNER, o);
    }

    public static HStyle roundCorner(double x, double y) {
        return new HStyle(HStyleType.ROUND_CORNER, new Point2D.Double(x, y));
    }

    public static HStyle threeD(Boolean b) {
        return new HStyle(HStyleType.THEED, b);
    }

    public static HStyle raised(Boolean b) {
        return new HStyle(HStyleType.RAISED, b);
    }

    public static HStyle layout(HLayout o) {
        return new HStyle(HStyleType.LAYOUT, o);
    }
}
