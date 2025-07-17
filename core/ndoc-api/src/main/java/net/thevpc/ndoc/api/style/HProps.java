package net.thevpc.ndoc.api.style;

import java.awt.*;

import net.thevpc.ndoc.api.model.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.model.elem2d.NDocAlign;
import net.thevpc.ndoc.api.model.elem2d.NDocSize;
import net.thevpc.nuts.elem.NElement;

/**
 * @author vpc
 */
public class HProps {

    public static HProp fontSize(Number value) {
        return new HProp(NDocPropName.FONT_SIZE, NElement.ofNumber(value == null ? null : value.doubleValue()));
    }

    public static HProp fontFamily(String value) {
        return new HProp(NDocPropName.FONT_FAMILY, NElement.ofString(value));
    }

    public static HProp width(NDocSize size) {
        return new HProp(NDocPropName.WIDTH, size);
    }

    public static HProp height(NDocSize size) {
        return new HProp(NDocPropName.HEIGHT, size);
    }

//    public static HProp foregroundColor(Paint color) {
//        return new HProp(HPropName.FOREGROUND_COLOR, color);
//    }

    public static HProp foregroundColor(int color) {
        return new HProp(NDocPropName.FOREGROUND_COLOR, NElement.ofInt(color));
    }

    public static HProp backgroundColor(int rgb) {
        return new HProp(NDocPropName.BACKGROUND_COLOR, NElement.ofInt(rgb));
    }

    public static HProp backgroundColor(Color color) {
        return new HProp(NDocPropName.BACKGROUND_COLOR, color==null?null: NElement.ofInt(color.getRGB()));
    }

//    public static HProp lineColor(Paint color) {
//        return new HProp(HPropName.LINE_COLOR, color);
//    }

    public static HProp gridColor(Color color) {
        return new HProp(NDocPropName.GRID_COLOR, color==null?null: NElement.ofInt(color.getRGB()));
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
        return new HProp(NDocPropName.PRESERVE_ASPECT_RATIO, NElement.ofBoolean(value));
    }

    public static HProp drawContour(boolean value) {
        return new HProp(NDocPropName.DRAW_CONTOUR, NElement.ofBoolean(value));
    }

    public static HProp drawGrid(boolean value) {
        return new HProp(NDocPropName.DRAW_GRID, NElement.ofBoolean(value));
    }

    public static HProp fillBackground(boolean value) {
        return new HProp(NDocPropName.FILL_BACKGROUND, NElement.ofBoolean(value));
    }

    public static HProp fontBold(boolean value) {
        return new HProp(NDocPropName.FONT_BOLD, NElement.ofBoolean(value));
    }

    public static HProp fontItalic(boolean value) {
        return new HProp(NDocPropName.FONT_ITALIC, NElement.ofBoolean(value));
    }

    public static HProp fontUnderlined(boolean value) {
        return new HProp(NDocPropName.FONT_UNDERLINED, NElement.ofBoolean(value));
    }

    public static HProp fontStrike(boolean value) {
        return new HProp(NDocPropName.FONT_STRIKE, NElement.ofBoolean(value));
    }

    public static HProp position(NDocDouble2 p) {
        return new HProp(NDocPropName.POSITION, p);
    }

    public static HProp center() {
        return origin(NDocAlign.CENTER);
    }

    public static HProp origin(NDocAlign p) {
        return new HProp(NDocPropName.ORIGIN, p);
    }

    public static HProp origin(double x, double y) {
        return new HProp(NDocPropName.ORIGIN, new NDocDouble2(x, y));
    }

    public static HProp position(double x, double y) {
        return new HProp(NDocPropName.POSITION, new NDocDouble2(x, y));
    }

    public static HProp position(NDocAlign p) {
        return new HProp(NDocPropName.POSITION, p);
    }

    public static HProp size(double x, double y) {
        return new HProp(NDocPropName.SIZE, new NDocDouble2(x, y));
    }

    public static HProp size(double x) {
        return new HProp(NDocPropName.SIZE, new NDocDouble2(x, x));
    }

    public static HProp size(NDocDouble2 size) {
        return new HProp(NDocPropName.SIZE, size);
    }

    public static HProp roundCorner(NDocDouble2 o) {
        return new HProp(NDocPropName.ROUND_CORNER, o);
    }

    public static HProp roundCorner(double x, double y) {
        return new HProp(NDocPropName.ROUND_CORNER, new NDocDouble2(x, y));
    }

    public static HProp threeD(Boolean b) {
        return new HProp(NDocPropName.THEED, NElement.ofBoolean(b));
    }

    public static HProp raised(Boolean b) {
        return new HProp(NDocPropName.RAISED, NElement.ofBoolean(b));
    }

    public static HProp columns(int o) {
        return new HProp(NDocPropName.COLUMNS, NElement.ofInt(o));
    }

    public static HProp rows(int o) {
        return new HProp(NDocPropName.ROWS, NElement.ofInt(o));
    }

    public static HProp rowsWeight(double... w) {
        return new HProp(NDocPropName.ROWS_WEIGHT, NElement.ofDoubleArray(w));
    }

    public static HProp colspan(int c) {
        return new HProp(NDocPropName.COLSPAN, NElement.ofInt(c));
    }

    public static HProp rowspan(int r) {
        return new HProp(NDocPropName.ROWSPAN, NElement.ofInt(r));
    }

    public static HProp styleClasses(String... array) {
        return new HProp(NDocPropName.CLASS, NElement.ofStringArray(array));
    }

    public static HProp template(boolean b) {
        return new HProp(NDocPropName.TEMPLATE, NElement.ofBoolean(b));
    }

    public static HProp disabled(boolean b) {
        return new HProp(NDocPropName.HIDE, NElement.ofBoolean(b));
    }

    public static HProp ancestors(String[] ancestors) {
        return new HProp(NDocPropName.ANCESTORS, NElement.ofStringArray(ancestors));
    }

    public static HProp name(String name) {
        return new HProp(NDocPropName.NAME, NElement.ofString(name));
    }

    public static HProp maxX(double x) {
        return new HProp(NDocPropName.MAX_X, NElement.ofDouble(x));
    }

    public static HProp maxY(double x) {
        return new HProp(NDocPropName.MAX_Y, NElement.ofDouble(x));
    }
}
