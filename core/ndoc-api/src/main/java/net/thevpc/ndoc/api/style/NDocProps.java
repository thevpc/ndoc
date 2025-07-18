package net.thevpc.ndoc.api.style;

import java.awt.*;

import net.thevpc.ndoc.api.model.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.model.elem2d.NDocAlign;
import net.thevpc.ndoc.api.model.elem2d.NDocSize;
import net.thevpc.nuts.elem.NElement;

/**
 * @author vpc
 */
public class NDocProps {

    public static NDocProp fontSize(Number value) {
        return new NDocProp(NDocPropName.FONT_SIZE, NElement.ofNumber(value == null ? null : value.doubleValue()));
    }

    public static NDocProp fontFamily(String value) {
        return new NDocProp(NDocPropName.FONT_FAMILY, NElement.ofString(value));
    }

    public static NDocProp width(NDocSize size) {
        return new NDocProp(NDocPropName.WIDTH, size);
    }

    public static NDocProp height(NDocSize size) {
        return new NDocProp(NDocPropName.HEIGHT, size);
    }

//    public static HProp foregroundColor(Paint color) {
//        return new HProp(HPropName.FOREGROUND_COLOR, color);
//    }

    public static NDocProp foregroundColor(int color) {
        return new NDocProp(NDocPropName.FOREGROUND_COLOR, NElement.ofInt(color));
    }

    public static NDocProp backgroundColor(int rgb) {
        return new NDocProp(NDocPropName.BACKGROUND_COLOR, NElement.ofInt(rgb));
    }

    public static NDocProp backgroundColor(Color color) {
        return new NDocProp(NDocPropName.BACKGROUND_COLOR, color==null?null: NElement.ofInt(color.getRGB()));
    }

//    public static HProp lineColor(Paint color) {
//        return new HProp(HPropName.LINE_COLOR, color);
//    }

    public static NDocProp gridColor(Color color) {
        return new NDocProp(NDocPropName.GRID_COLOR, color==null?null: NElement.ofInt(color.getRGB()));
    }

//    public static HProp lineColor(int color) {
//        return new HProp(HPropName.LINE_COLOR, new Color(color));
//    }

    public static NDocProp fontBold() {
        return fontBold(true);
    }

    public static NDocProp preserveShapeRatio() {
        return preserveShapeRatio(true);
    }

    public static NDocProp preserveShapeRatio(boolean value) {
        return new NDocProp(NDocPropName.PRESERVE_ASPECT_RATIO, NElement.ofBoolean(value));
    }

    public static NDocProp drawContour(boolean value) {
        return new NDocProp(NDocPropName.DRAW_CONTOUR, NElement.ofBoolean(value));
    }

    public static NDocProp drawGrid(boolean value) {
        return new NDocProp(NDocPropName.DRAW_GRID, NElement.ofBoolean(value));
    }

    public static NDocProp fillBackground(boolean value) {
        return new NDocProp(NDocPropName.FILL_BACKGROUND, NElement.ofBoolean(value));
    }

    public static NDocProp fontBold(boolean value) {
        return new NDocProp(NDocPropName.FONT_BOLD, NElement.ofBoolean(value));
    }

    public static NDocProp fontItalic(boolean value) {
        return new NDocProp(NDocPropName.FONT_ITALIC, NElement.ofBoolean(value));
    }

    public static NDocProp fontUnderlined(boolean value) {
        return new NDocProp(NDocPropName.FONT_UNDERLINED, NElement.ofBoolean(value));
    }

    public static NDocProp fontStrike(boolean value) {
        return new NDocProp(NDocPropName.FONT_STRIKE, NElement.ofBoolean(value));
    }

    public static NDocProp position(NDocDouble2 p) {
        return new NDocProp(NDocPropName.POSITION, p);
    }

    public static NDocProp center() {
        return origin(NDocAlign.CENTER);
    }

    public static NDocProp origin(NDocAlign p) {
        return new NDocProp(NDocPropName.ORIGIN, p);
    }

    public static NDocProp origin(double x, double y) {
        return new NDocProp(NDocPropName.ORIGIN, new NDocDouble2(x, y));
    }

    public static NDocProp position(double x, double y) {
        return new NDocProp(NDocPropName.POSITION, new NDocDouble2(x, y));
    }

    public static NDocProp position(NDocAlign p) {
        return new NDocProp(NDocPropName.POSITION, p);
    }

    public static NDocProp size(double x, double y) {
        return new NDocProp(NDocPropName.SIZE, new NDocDouble2(x, y));
    }

    public static NDocProp size(double x) {
        return new NDocProp(NDocPropName.SIZE, new NDocDouble2(x, x));
    }

    public static NDocProp size(NDocDouble2 size) {
        return new NDocProp(NDocPropName.SIZE, size);
    }

    public static NDocProp roundCorner(NDocDouble2 o) {
        return new NDocProp(NDocPropName.ROUND_CORNER, o);
    }

    public static NDocProp roundCorner(double x, double y) {
        return new NDocProp(NDocPropName.ROUND_CORNER, new NDocDouble2(x, y));
    }

    public static NDocProp threeD(Boolean b) {
        return new NDocProp(NDocPropName.THEED, NElement.ofBoolean(b));
    }

    public static NDocProp raised(Boolean b) {
        return new NDocProp(NDocPropName.RAISED, NElement.ofBoolean(b));
    }

    public static NDocProp columns(int o) {
        return new NDocProp(NDocPropName.COLUMNS, NElement.ofInt(o));
    }

    public static NDocProp rows(int o) {
        return new NDocProp(NDocPropName.ROWS, NElement.ofInt(o));
    }

    public static NDocProp rowsWeight(double... w) {
        return new NDocProp(NDocPropName.ROWS_WEIGHT, NElement.ofDoubleArray(w));
    }

    public static NDocProp colspan(int c) {
        return new NDocProp(NDocPropName.COLSPAN, NElement.ofInt(c));
    }

    public static NDocProp rowspan(int r) {
        return new NDocProp(NDocPropName.ROWSPAN, NElement.ofInt(r));
    }

    public static NDocProp styleClasses(String... array) {
        return new NDocProp(NDocPropName.CLASS, NElement.ofStringArray(array));
    }

    public static NDocProp disabled(boolean b) {
        return new NDocProp(NDocPropName.HIDE, NElement.ofBoolean(b));
    }

    public static NDocProp ancestors(String[] ancestors) {
        return new NDocProp(NDocPropName.ANCESTORS, NElement.ofStringArray(ancestors));
    }

    public static NDocProp name(String name) {
        return new NDocProp(NDocPropName.NAME, NElement.ofString(name));
    }

    public static NDocProp maxX(double x) {
        return new NDocProp(NDocPropName.MAX_X, NElement.ofDouble(x));
    }

    public static NDocProp maxY(double x) {
        return new NDocProp(NDocPropName.MAX_Y, NElement.ofDouble(x));
    }
}
