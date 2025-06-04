package net.thevpc.ndoc.api.style;

import java.awt.*;

import net.thevpc.ndoc.api.model.elem2d.Double2;
import net.thevpc.ndoc.api.model.elem2d.HAlign;
import net.thevpc.ndoc.api.model.elem2d.HSize;
import net.thevpc.nuts.elem.NElement;

/**
 * @author vpc
 */
public class HProps {

    public static HProp fontSize(Number value) {
        return new HProp(HPropName.FONT_SIZE, NElement.ofNumber(value == null ? null : value.doubleValue()));
    }

    public static HProp fontFamily(String value) {
        return new HProp(HPropName.FONT_FAMILY, NElement.ofString(value));
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
        return new HProp(HPropName.FOREGROUND_COLOR, NElement.ofInt(color));
    }

    public static HProp backgroundColor(int rgb) {
        return new HProp(HPropName.BACKGROUND_COLOR, NElement.ofInt(rgb));
    }

    public static HProp backgroundColor(Color color) {
        return new HProp(HPropName.BACKGROUND_COLOR, color==null?null: NElement.ofInt(color.getRGB()));
    }

//    public static HProp lineColor(Paint color) {
//        return new HProp(HPropName.LINE_COLOR, color);
//    }

    public static HProp gridColor(Color color) {
        return new HProp(HPropName.GRID_COLOR, color==null?null: NElement.ofInt(color.getRGB()));
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
        return new HProp(HPropName.PRESERVE_ASPECT_RATIO, NElement.ofBoolean(value));
    }

    public static HProp drawContour(boolean value) {
        return new HProp(HPropName.DRAW_CONTOUR, NElement.ofBoolean(value));
    }

    public static HProp drawGrid(boolean value) {
        return new HProp(HPropName.DRAW_GRID, NElement.ofBoolean(value));
    }

    public static HProp fillBackground(boolean value) {
        return new HProp(HPropName.FILL_BACKGROUND, NElement.ofBoolean(value));
    }

    public static HProp fontBold(boolean value) {
        return new HProp(HPropName.FONT_BOLD, NElement.ofBoolean(value));
    }

    public static HProp fontItalic(boolean value) {
        return new HProp(HPropName.FONT_ITALIC, NElement.ofBoolean(value));
    }

    public static HProp fontUnderlined(boolean value) {
        return new HProp(HPropName.FONT_UNDERLINED, NElement.ofBoolean(value));
    }

    public static HProp fontStrike(boolean value) {
        return new HProp(HPropName.FONT_STRIKE, NElement.ofBoolean(value));
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
        return new HProp(HPropName.THEED, NElement.ofBoolean(b));
    }

    public static HProp raised(Boolean b) {
        return new HProp(HPropName.RAISED, NElement.ofBoolean(b));
    }

    public static HProp columns(int o) {
        return new HProp(HPropName.COLUMNS, NElement.ofInt(o));
    }

    public static HProp rows(int o) {
        return new HProp(HPropName.ROWS, NElement.ofInt(o));
    }

    public static HProp rowsWeight(double... w) {
        return new HProp(HPropName.ROWS_WEIGHT, NElement.ofDoubleArray(w));
    }

    public static HProp colspan(int c) {
        return new HProp(HPropName.COLSPAN, NElement.ofInt(c));
    }

    public static HProp rowspan(int r) {
        return new HProp(HPropName.ROWSPAN, NElement.ofInt(r));
    }

    public static HProp styleClasses(String... array) {
        return new HProp(HPropName.CLASS, NElement.ofStringArray(array));
    }

    public static HProp template(boolean b) {
        return new HProp(HPropName.TEMPLATE, NElement.ofBoolean(b));
    }

    public static HProp disabled(boolean b) {
        return new HProp(HPropName.HIDE, NElement.ofBoolean(b));
    }

    public static HProp ancestors(String[] ancestors) {
        return new HProp(HPropName.ANCESTORS, NElement.ofStringArray(ancestors));
    }

    public static HProp name(String name) {
        return new HProp(HPropName.NAME, NElement.ofString(name));
    }

    public static HProp maxX(double x) {
        return new HProp(HPropName.MAX_X, NElement.ofDouble(x));
    }

    public static HProp maxY(double x) {
        return new HProp(HPropName.MAX_Y, NElement.ofDouble(x));
    }
}
