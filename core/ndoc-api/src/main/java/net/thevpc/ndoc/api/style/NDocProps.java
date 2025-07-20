package net.thevpc.ndoc.api.style;

import java.awt.*;

import net.thevpc.ndoc.api.model.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.model.elem2d.NDocAlign;
import net.thevpc.ndoc.api.model.elem2d.NDocSize;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NToElement;

/**
 * @author vpc
 */
public class NDocProps {

    public static NDocProp fontSize(Number value) {
        return elem(NDocPropName.FONT_SIZE, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofNumber(value == null ? null : value.doubleValue())));
    }

    public static NDocProp fontFamily(String value) {
        return elem(NDocPropName.FONT_FAMILY, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofString(value)));
    }

    public static NDocProp width(NDocSize size) {
        return elem(NDocPropName.WIDTH, size);
    }

    public static NDocProp height(NDocSize size) {
        return elem(NDocPropName.HEIGHT, size);
    }

//    public static HProp foregroundColor(Paint color) {
//        return new HProp(HPropName.FOREGROUND_COLOR, color);
//    }

    public static NDocProp foregroundColor(int color) {
        return elem(NDocPropName.FOREGROUND_COLOR, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofInt(color)));
    }

    public static NDocProp backgroundColor(int rgb) {
        return elem(NDocPropName.BACKGROUND_COLOR, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofInt(rgb)));
    }

    public static NDocProp backgroundColor(Color color) {
        return elem(NDocPropName.BACKGROUND_COLOR, color == null ? null : NDocUtils.addCompilerDeclarationPathDummy(NElement.ofInt(color.getRGB())));
    }

//    public static HProp lineColor(Paint color) {
//        return new HProp(HPropName.LINE_COLOR, color);
//    }

    public static NDocProp gridColor(Color color) {
        return elem(NDocPropName.GRID_COLOR, color == null ? null : NDocUtils.addCompilerDeclarationPathDummy(NElement.ofInt(color.getRGB())));
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
        return elem(NDocPropName.PRESERVE_ASPECT_RATIO, NDocUtils.addCompilerDeclarationPathDummy((NElement.ofBoolean(value))));
    }

    public static NDocProp drawContour(boolean value) {
        return elem(NDocPropName.DRAW_CONTOUR, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(value)));
    }

    public static NDocProp drawGrid(boolean value) {
        return elem(NDocPropName.DRAW_GRID, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(value)));
    }

    public static NDocProp fillBackground(boolean value) {
        return elem(NDocPropName.FILL_BACKGROUND, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(value)));
    }

    public static NDocProp fontBold(boolean value) {
        return elem(NDocPropName.FONT_BOLD, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(value)));
    }

    public static NDocProp fontItalic(boolean value) {
        return elem(NDocPropName.FONT_ITALIC, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(value)));
    }

    public static NDocProp fontUnderlined(boolean value) {
        return elem(NDocPropName.FONT_UNDERLINED, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(value)));
    }

    public static NDocProp fontStrike(boolean value) {
        return elem(NDocPropName.FONT_STRIKE, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(value)));
    }

    public static NDocProp position(NDocDouble2 p) {
        return elem(NDocPropName.POSITION, p);
    }

    public static NDocProp center() {
        return origin(NDocAlign.CENTER);
    }

    public static NDocProp origin(NDocAlign p) {
        return elem(NDocPropName.ORIGIN, p);
    }

    public static NDocProp origin(double x, double y) {
        return elem(NDocPropName.ORIGIN, new NDocDouble2(x, y));
    }

    public static NDocProp position(double x, double y) {
        return elem(NDocPropName.POSITION, new NDocDouble2(x, y));
    }

    public static NDocProp position(NDocAlign p) {
        return elem(NDocPropName.POSITION, p);
    }

    public static NDocProp size(double x, double y) {
        return elem(NDocPropName.SIZE, new NDocDouble2(x, y));
    }

    public static NDocProp size(double x) {
        return elem(NDocPropName.SIZE, new NDocDouble2(x, x));
    }

    public static NDocProp size(NDocDouble2 size) {
        return elem(NDocPropName.SIZE, size);
    }

    public static NDocProp roundCorner(NDocDouble2 o) {
        return elem(NDocPropName.ROUND_CORNER, o);
    }

    public static NDocProp roundCorner(double x, double y) {
        return elem(NDocPropName.ROUND_CORNER, NDocUtils.addCompilerDeclarationPathDummy(new NDocDouble2(x, y).toElement()));
    }

    public static NDocProp threeD(Boolean b) {
        return elem(NDocPropName.THEED, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(b)));
    }

    public static NDocProp raised(Boolean b) {
        return elem(NDocPropName.RAISED, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(b)));
    }

    public static NDocProp columns(int o) {
        return elem(NDocPropName.COLUMNS, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofInt(o)));
    }

    public static NDocProp rows(int o) {
        return elem(NDocPropName.ROWS, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofInt(o)));
    }

    public static NDocProp rowsWeight(double... w) {
        return elem(NDocPropName.ROWS_WEIGHT, NElement.ofDoubleArray(w));
    }

    public static NDocProp colspan(int c) {
        return elem(NDocPropName.COLSPAN, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofInt(c)));
    }

    public static NDocProp rowspan(int r) {
        return elem(NDocPropName.ROWSPAN, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofInt(r)));
    }

    public static NDocProp styleClasses(String... array) {
        return elem(NDocPropName.CLASS, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofStringArray(array)));
    }

    public static NDocProp disabled(boolean b) {
        return elem(NDocPropName.HIDE, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(b)));
    }

    public static NDocProp name(String name) {
        return elem(NDocPropName.NAME, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofString(name)));
    }

    public static NDocProp maxX(double x) {
        return elem(NDocPropName.MAX_X, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofDouble(x)));
    }

    public static NDocProp maxY(double x) {
        return elem(NDocPropName.MAX_Y, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofDouble(x)));
    }

    public static NDocProp elem(String name, NElement v) {
        return new NDocProp(name, v, null);
    }

    public static NDocProp elem(String name, NToElement v) {
        return new NDocProp(name,(v==null?null:NDocUtils.addCompilerDeclarationPathDummy(v.toElement())), null);
    }
}
