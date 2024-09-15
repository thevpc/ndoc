package net.thevpc.halfa.api.style;

import java.awt.*;

import net.thevpc.halfa.api.model.elem2d.Double2;
import net.thevpc.halfa.api.model.elem2d.HAlign;
import net.thevpc.halfa.api.model.elem2d.HSize;
import net.thevpc.tson.Tson;

/**
 * @author vpc
 */
public class HProps {

    public static HProp fontSize(Number value) {
        return new HProp(HPropName.FONT_SIZE, Tson.of(value == null ? null : value.doubleValue()));
    }

    public static HProp fontFamily(String value) {
        return new HProp(HPropName.FONT_FAMILY, Tson.of(value));
    }

    public static HProp width(HSize size) {
        return new HProp(HPropName.WIDTH, size);
    }

    public static HProp height(HSize size) {
        return new HProp(HPropName.HEIGHT, size);
    }

//    public static HProp foregroundColor(Paint color) {
//        return new HProp(HPropName.FOREGROUND_COLOR, color);
//    }

    public static HProp foregroundColor(int color) {
        return new HProp(HPropName.FOREGROUND_COLOR, Tson.of(color));
    }

    public static HProp backgroundColor(int rgb) {
        return new HProp(HPropName.BACKGROUND_COLOR, Tson.of(rgb));
    }

    public static HProp backgroundColor(Color color) {
        return new HProp(HPropName.BACKGROUND_COLOR, color==null?null:Tson.of(color.getRGB()));
    }

//    public static HProp lineColor(Paint color) {
//        return new HProp(HPropName.LINE_COLOR, color);
//    }

    public static HProp gridColor(Color color) {
        return new HProp(HPropName.GRID_COLOR, color==null?null:Tson.of(color.getRGB()));
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
        return new HProp(HPropName.PRESERVE_ASPECT_RATIO, Tson.of(value));
    }

    public static HProp drawContour(boolean value) {
        return new HProp(HPropName.DRAW_CONTOUR, Tson.of(value));
    }

    public static HProp drawGrid(boolean value) {
        return new HProp(HPropName.DRAW_GRID, Tson.of(value));
    }

    public static HProp fillBackground(boolean value) {
        return new HProp(HPropName.FILL_BACKGROUND, Tson.of(value));
    }

    public static HProp fontBold(boolean value) {
        return new HProp(HPropName.FONT_BOLD, Tson.of(value));
    }

    public static HProp fontItalic(boolean value) {
        return new HProp(HPropName.FONT_ITALIC, Tson.of(value));
    }

    public static HProp fontUnderlined(boolean value) {
        return new HProp(HPropName.FONT_UNDERLINED, Tson.of(value));
    }

    public static HProp fontStrike(boolean value) {
        return new HProp(HPropName.FONT_STRIKE, Tson.of(value));
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
        return new HProp(HPropName.THEED, Tson.of(b));
    }

    public static HProp raised(Boolean b) {
        return new HProp(HPropName.RAISED, Tson.of(b));
    }

    public static HProp columns(int o) {
        return new HProp(HPropName.COLUMNS, Tson.of(o));
    }

    public static HProp rows(int o) {
        return new HProp(HPropName.ROWS, Tson.of(o));
    }

    public static HProp rowsWeight(double... w) {
        return new HProp(HPropName.ROWS_WEIGHT, Tson.of(w));
    }

    public static HProp colspan(int c) {
        return new HProp(HPropName.COLSPAN, Tson.of(c));
    }

    public static HProp rowspan(int r) {
        return new HProp(HPropName.ROWSPAN, Tson.of(r));
    }

    public static HProp styleClasses(String... array) {
        return new HProp(HPropName.CLASS, Tson.of(array));
    }

    public static HProp template(boolean b) {
        return new HProp(HPropName.TEMPLATE, Tson.of(b));
    }

    public static HProp disabled(boolean b) {
        return new HProp(HPropName.HIDE, Tson.of(b));
    }

    public static HProp ancestors(String[] ancestors) {
        return new HProp(HPropName.ANCESTORS, Tson.of(ancestors));
    }

    public static HProp name(String name) {
        return new HProp(HPropName.NAME, Tson.of(name));
    }

    public static HProp maxX(double x) {
        return new HProp(HPropName.MAX_X, Tson.of(x));
    }

    public static HProp maxY(double x) {
        return new HProp(HPropName.MAX_Y, Tson.of(x));
    }
}
