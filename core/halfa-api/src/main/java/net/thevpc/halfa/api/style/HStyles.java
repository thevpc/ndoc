package net.thevpc.halfa.api.style;

import java.awt.*;

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

    public static HStyle foregroundColor(int color) {
        return new HStyle(HStyleType.FOREGROUND_COLOR, new Color(color));
    }

    public static HStyle backgroundColor(int rgb) {
        return backgroundColor(new Color(rgb));
    }

    public static HStyle backgroundColor(Paint color) {
        return new HStyle(HStyleType.BACKGROUND_COLOR, color);
    }

    public static HStyle lineColor(Paint color) {
        return new HStyle(HStyleType.LINE_COLOR, color);
    }

    public static HStyle gridColor(Paint color) {
        return new HStyle(HStyleType.GRID_COLOR, color);
    }

    public static HStyle lineColor(int color) {
        return new HStyle(HStyleType.LINE_COLOR, new Color(color));
    }

    public static HStyle fontBold() {
        return fontBold(true);
    }

    public static HStyle preserveShapeRatio() {
        return preserveShapeRatio(true);
    }

    public static HStyle preserveShapeRatio(boolean value) {
        return new HStyle(HStyleType.PRESERVE_SHAPE_RATIO, value);
    }

    public static HStyle drawContour(boolean value) {
        return new HStyle(HStyleType.DRAW_CONTOUR, value);
    }

    public static HStyle drawGrid(boolean value) {
        return new HStyle(HStyleType.DRAW_GRID, value);
    }

    public static HStyle fillBackground(boolean value) {
        return new HStyle(HStyleType.FILL_BACKGROUND, value);
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

    public static HStyle position(Double2 p) {
        return new HStyle(HStyleType.POSITION, p);
    }

    public static HStyle center() {
        return origin(HAlign.CENTER);
    }

    public static HStyle origin(HAlign p) {
        return new HStyle(HStyleType.ORIGIN, p);
    }

    public static HStyle origin(double x, double y) {
        return new HStyle(HStyleType.ORIGIN, new Double2(x, y));
    }

    public static HStyle position(double x, double y) {
        return new HStyle(HStyleType.POSITION, new Double2(x, y));
    }
    public static HStyle position(HAlign p) {
        return new HStyle(HStyleType.POSITION, p);
    }

    public static HStyle size(double x, double y) {
        return new HStyle(HStyleType.SIZE, new Double2(x, y));
    }

    public static HStyle size(Double2 size) {
        return new HStyle(HStyleType.SIZE, size);
    }

    public static HStyle roundCorner(Double2 o) {
        return new HStyle(HStyleType.ROUND_CORNER, o);
    }

    public static HStyle roundCorner(double x, double y) {
        return new HStyle(HStyleType.ROUND_CORNER, new Double2(x, y));
    }

    public static HStyle threeD(Boolean b) {
        return new HStyle(HStyleType.THEED, b);
    }

    public static HStyle raised(Boolean b) {
        return new HStyle(HStyleType.RAISED, b);
    }

    public static HStyle columns(int o) {
        return new HStyle(HStyleType.COLUMNS, o);
    }

    public static HStyle rows(int o) {
        return new HStyle(HStyleType.ROWS, o);
    }

    public static HStyle rowsWeight(double... w) {
        return new HStyle(HStyleType.ROWS_WEIGHT, w);
    }

    public static HStyle columnsWeight(double... w) {
        return new HStyle(HStyleType.COLUMNS_WEIGHT, w);
    }

    public static HStyle colspan(int c) {
        return new HStyle(HStyleType.COLSPAN, c);
    }

    public static HStyle rowspan(int r) {
        return new HStyle(HStyleType.ROWSPAN, r);
    }

    public static HStyle styleClasses(String... array) {
        return new HStyle(HStyleType.STYLE_CLASS, array);
    }

    public static HStyle template(boolean b) {
        return new HStyle(HStyleType.TEMPLATE, b);
    }
    public static HStyle disabled(boolean b) {
        return new HStyle(HStyleType.DISABLED, b);
    }
    public static HStyle inherits(String templateName) {
        return new HStyle(HStyleType.EXTENDS, templateName);
    }
    public static HStyle name(String name) {
        return new HStyle(HStyleType.NAME, name);
    }
}
