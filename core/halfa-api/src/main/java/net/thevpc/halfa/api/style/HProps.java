package net.thevpc.halfa.api.style;

import java.awt.*;

import net.thevpc.halfa.api.model.elem2d.Double2;
import net.thevpc.halfa.api.model.elem2d.HAlign;
import net.thevpc.halfa.api.model.elem2d.HSize;

/**
 * @author vpc
 */
public class HProps {

    public static HProp fontSize(Number value) {
        return new HProp(HPropName.FONT_SIZE, value == null ? null : value.doubleValue());
    }

    public static HProp fontFamily(String value) {
        return new HProp(HPropName.FONT_FAMILY, value);
    }

    public static HProp width(HSize size) {
        return new HProp(HPropName.WIDTH, size);
    }

    public static HProp height(HSize size) {
        return new HProp(HPropName.HEIGHT, size);
    }

    public static HProp foregroundColor(Paint color) {
        return new HProp(HPropName.FOREGROUND_COLOR, color);
    }

    public static HProp foregroundColor(int color) {
        return new HProp(HPropName.FOREGROUND_COLOR, new Color(color));
    }

    public static HProp backgroundColor(int rgb) {
        return backgroundColor(new Color(rgb));
    }

    public static HProp backgroundColor(Paint color) {
        return new HProp(HPropName.BACKGROUND_COLOR, color);
    }

//    public static HProp lineColor(Paint color) {
//        return new HProp(HPropName.LINE_COLOR, color);
//    }

    public static HProp gridColor(Paint color) {
        return new HProp(HPropName.GRID_COLOR, color);
    }

//    public static HProp lineColor(int color) {
//        return new HProp(HPropName.LINE_COLOR, new Color(color));
//    }

    public static HProp fontBold() {
        return fontBold(true);
    }

    public static HProp preserveShapeRatio() {
        return preserveShapeRatio(true);
    }

    public static HProp preserveShapeRatio(boolean value) {
        return new HProp(HPropName.PRESERVE_ASPECT_RATIO, value);
    }

    public static HProp drawContour(boolean value) {
        return new HProp(HPropName.DRAW_CONTOUR, value);
    }

    public static HProp drawGrid(boolean value) {
        return new HProp(HPropName.DRAW_GRID, value);
    }

    public static HProp fillBackground(boolean value) {
        return new HProp(HPropName.FILL_BACKGROUND, value);
    }

    public static HProp fontBold(boolean value) {
        return new HProp(HPropName.FONT_BOLD, value);
    }

    public static HProp fontItalic(boolean value) {
        return new HProp(HPropName.FONT_ITALIC, value);
    }

    public static HProp fontUnderlined(boolean value) {
        return new HProp(HPropName.FONT_UNDERLINED, value);
    }

    public static HProp fontStrike(boolean value) {
        return new HProp(HPropName.FONT_STRIKE, value);
    }

    public static HProp position(Double2 p) {
        return new HProp(HPropName.POSITION, p);
    }

    public static HProp center() {
        return origin(HAlign.CENTER);
    }

    public static HProp origin(HAlign p) {
        return new HProp(HPropName.ORIGIN, p);
    }

    public static HProp origin(double x, double y) {
        return new HProp(HPropName.ORIGIN, new Double2(x, y));
    }

    public static HProp position(double x, double y) {
        return new HProp(HPropName.POSITION, new Double2(x, y));
    }

    public static HProp position(HAlign p) {
        return new HProp(HPropName.POSITION, p);
    }

    public static HProp size(double x, double y) {
        return new HProp(HPropName.SIZE, new Double2(x, y));
    }

    public static HProp size(double x) {
        return new HProp(HPropName.SIZE, new Double2(x, x));
    }

    public static HProp size(Double2 size) {
        return new HProp(HPropName.SIZE, size);
    }

    public static HProp roundCorner(Double2 o) {
        return new HProp(HPropName.ROUND_CORNER, o);
    }

    public static HProp roundCorner(double x, double y) {
        return new HProp(HPropName.ROUND_CORNER, new Double2(x, y));
    }

    public static HProp threeD(Boolean b) {
        return new HProp(HPropName.THEED, b);
    }

    public static HProp raised(Boolean b) {
        return new HProp(HPropName.RAISED, b);
    }

    public static HProp columns(int o) {
        return new HProp(HPropName.COLUMNS, o);
    }

    public static HProp rows(int o) {
        return new HProp(HPropName.ROWS, o);
    }

    public static HProp rowsWeight(double... w) {
        return new HProp(HPropName.ROWS_WEIGHT, w);
    }

    public static HProp columnsWeight(double... w) {
        return new HProp(HPropName.COLUMNS_WEIGHT, w);
    }

    public static HProp colspan(int c) {
        return new HProp(HPropName.COLSPAN, c);
    }

    public static HProp rowspan(int r) {
        return new HProp(HPropName.ROWSPAN, r);
    }

    public static HProp styleClasses(String... array) {
        return new HProp(HPropName.CLASS, array);
    }

    public static HProp template(boolean b) {
        return new HProp(HPropName.TEMPLATE, b);
    }

    public static HProp disabled(boolean b) {
        return new HProp(HPropName.HIDE, b);
    }

    public static HProp ancestors(String[] ancestors) {
        return new HProp(HPropName.ANCESTORS, ancestors);
    }

    public static HProp name(String name) {
        return new HProp(HPropName.NAME, name);
    }

    public static HProp maxX(double x) {
        return new HProp(HPropName.MAX_X, x);
    }

    public static HProp maxY(double x) {
        return new HProp(HPropName.MAX_Y, x);
    }
}
