package net.thevpc.ntexup.api.document.style;

import java.awt.*;

import net.thevpc.ntexup.api.document.elem2d.NDocDouble2;
import net.thevpc.ntexup.api.document.elem2d.NDocAlign;
import net.thevpc.ntexup.api.document.elem2d.NDocSize;
import net.thevpc.ntexup.api.util.NDocUtils;
import net.thevpc.nuts.elem.NElement;

/**
 * @author vpc
 */
public class NDocProps {

    public static NTxProp fontSize(Number value) {
        return NTxProp.of(NDocPropName.FONT_SIZE, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofNumber(value == null ? null : value.doubleValue())));
    }

    public static NTxProp fontFamily(String value) {
        return NTxProp.of(NDocPropName.FONT_FAMILY, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofString(value)));
    }

    public static NTxProp width(NDocSize size) {
        return NTxProp.of(NDocPropName.WIDTH, size);
    }

    public static NTxProp height(NDocSize size) {
        return NTxProp.of(NDocPropName.HEIGHT, size);
    }

//    public static HProp foregroundColor(Paint color) {
//        return new HProp(HPropName.FOREGROUND_COLOR, color);
//    }

    public static NTxProp foregroundColor(int color) {
        return NTxProp.of(NDocPropName.FOREGROUND_COLOR, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofInt(color)));
    }

    public static NTxProp backgroundColor(int rgb) {
        return NTxProp.of(NDocPropName.BACKGROUND_COLOR, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofInt(rgb)));
    }

    public static NTxProp backgroundColor(Color color) {
        return NTxProp.of(NDocPropName.BACKGROUND_COLOR, color == null ? null : NDocUtils.addCompilerDeclarationPathDummy(NElement.ofInt(color.getRGB())));
    }

//    public static HProp lineColor(Paint color) {
//        return new HProp(HPropName.LINE_COLOR, color);
//    }

    public static NTxProp gridColor(Color color) {
        return NTxProp.of(NDocPropName.GRID_COLOR, color == null ? null : NDocUtils.addCompilerDeclarationPathDummy(NElement.ofInt(color.getRGB())));
    }

//    public static HProp lineColor(int color) {
//        return new HProp(HPropName.LINE_COLOR, new Color(color));
//    }

    public static NTxProp fontBold() {
        return fontBold(true);
    }

    public static NTxProp preserveShapeRatio() {
        return preserveShapeRatio(true);
    }

    public static NTxProp preserveShapeRatio(boolean value) {
        return NTxProp.of(NDocPropName.PRESERVE_ASPECT_RATIO, NDocUtils.addCompilerDeclarationPathDummy((NElement.ofBoolean(value))));
    }

    public static NTxProp drawContour(boolean value) {
        return NTxProp.of(NDocPropName.DRAW_CONTOUR, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(value)));
    }

    public static NTxProp drawGrid(boolean value) {
        return NTxProp.of(NDocPropName.DRAW_GRID, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(value)));
    }

    public static NTxProp fillBackground(boolean value) {
        return NTxProp.of(NDocPropName.FILL_BACKGROUND, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(value)));
    }

    public static NTxProp fontBold(boolean value) {
        return NTxProp.of(NDocPropName.FONT_BOLD, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(value)));
    }

    public static NTxProp fontItalic(boolean value) {
        return NTxProp.of(NDocPropName.FONT_ITALIC, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(value)));
    }

    public static NTxProp fontUnderlined(boolean value) {
        return NTxProp.of(NDocPropName.FONT_UNDERLINED, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(value)));
    }

    public static NTxProp fontStrike(boolean value) {
        return NTxProp.of(NDocPropName.FONT_STRIKE, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(value)));
    }

    public static NTxProp position(NDocDouble2 p) {
        return NTxProp.of(NDocPropName.POSITION, p);
    }

    public static NTxProp center() {
        return origin(NDocAlign.CENTER);
    }

    public static NTxProp origin(NDocAlign p) {
        return NTxProp.of(NDocPropName.ORIGIN, p);
    }

    public static NTxProp origin(double x, double y) {
        return NTxProp.of(NDocPropName.ORIGIN, new NDocDouble2(x, y));
    }

    public static NTxProp position(double x, double y) {
        return NTxProp.of(NDocPropName.POSITION, new NDocDouble2(x, y));
    }

    public static NTxProp position(NDocAlign p) {
        return NTxProp.of(NDocPropName.POSITION, p);
    }

    public static NTxProp size(double x, double y) {
        return NTxProp.of(NDocPropName.SIZE, new NDocDouble2(x, y));
    }

    public static NTxProp size(double x) {
        return NTxProp.of(NDocPropName.SIZE, new NDocDouble2(x, x));
    }

    public static NTxProp size(NDocDouble2 size) {
        return NTxProp.of(NDocPropName.SIZE, size);
    }

    public static NTxProp roundCorner(NDocDouble2 o) {
        return NTxProp.of(NDocPropName.ROUND_CORNER, o);
    }

    public static NTxProp roundCorner(double x, double y) {
        return NTxProp.of(NDocPropName.ROUND_CORNER, NDocUtils.addCompilerDeclarationPathDummy(new NDocDouble2(x, y).toElement()));
    }

    public static NTxProp threeD(Boolean b) {
        return NTxProp.of(NDocPropName.THEED, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(b)));
    }

    public static NTxProp raised(Boolean b) {
        return NTxProp.of(NDocPropName.RAISED, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(b)));
    }

    public static NTxProp columns(int o) {
        return NTxProp.of(NDocPropName.COLUMNS, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofInt(o)));
    }

    public static NTxProp rows(int o) {
        return NTxProp.of(NDocPropName.ROWS, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofInt(o)));
    }

    public static NTxProp rowsWeight(double... w) {
        return NTxProp.of(NDocPropName.ROWS_WEIGHT, NElement.ofDoubleArray(w));
    }

    public static NTxProp colspan(int c) {
        return NTxProp.of(NDocPropName.COLSPAN, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofInt(c)));
    }

    public static NTxProp rowspan(int r) {
        return NTxProp.of(NDocPropName.ROWSPAN, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofInt(r)));
    }

    public static NTxProp styleClasses(String... array) {
        return NTxProp.of(NDocPropName.CLASS, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofStringArray(array)));
    }

    public static NTxProp disabled(boolean b) {
        return NTxProp.of(NDocPropName.HIDE, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofBoolean(b)));
    }

    public static NTxProp name(String name) {
        return NTxProp.of(NDocPropName.NAME, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofString(name)));
    }

    public static NTxProp maxX(double x) {
        return NTxProp.of(NDocPropName.MAX_X, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofDouble(x)));
    }

    public static NTxProp maxY(double x) {
        return NTxProp.of(NDocPropName.MAX_Y, NDocUtils.addCompilerDeclarationPathDummy(NElement.ofDouble(x)));
    }

}
