package net.thevpc.halfa.spi.eval;

import net.thevpc.halfa.api.model.HArrow;
import net.thevpc.halfa.api.model.HArrowType;
import net.thevpc.halfa.api.model.elem2d.*;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

import java.awt.*;

public class HValueByType {

    public static NOptional<HArrowType> getArrowType(HNode t, HNodeRendererContext ctx, String propName, String... propNames) {
        return ObjEx.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asArrowType();
    }

    public static NOptional<HArrow> getArrow(HNode t, HNodeRendererContext ctx, String propName, String... propNames) {
        return ObjEx.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asArrow();
    }

    public static NOptional<Boolean> getBoolean(HNode t, HNodeRendererContext ctx, String propName, String... propNames) {
        return ObjEx.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asBoolean();
    }

    public static NOptional<Integer> getIntOrBoolean(HNode t, HNodeRendererContext ctx, String propName, String... propNames) {
        return ObjEx.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asIntOrBoolean();
    }

    public static NOptional<Integer> getInt(HNode t, HNodeRendererContext ctx, String propName, String... propNames) {
        return ObjEx.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asInt();
    }

    public static NOptional<String> getStringOrName(HNode t, HNodeRendererContext ctx, String propName) {
        return ObjEx.of(ctx.computePropertyValue(t, propName).orNull()).asStringOrName();
    }

    public static NOptional<Paint> getPaint(HNode t, HNodeRendererContext ctx, String propName, String... propNames) {
        ObjEx r = ObjEx.of(ctx.computePropertyValue(t, propName, propNames).orElse(null));
        return NOptional.of(r.asPaint().orElse(null));
    }

    public static NOptional<Color> getColor(HNode t, HNodeRendererContext ctx, String propName, String... propNames) {
        ObjEx r = ObjEx.of(ctx.computePropertyValue(t, propName, propNames).orElse(null));
        return NOptional.of(r.asColor().orElse(null));
    }

    public static NOptional<Double> getDouble(HNode t, HNodeRendererContext ctx, String s) {
        return ObjEx.of(ctx.computePropertyValue(t, s).orNull()).asDouble();
    }

    public static NOptional<Double2> getDouble2(HNode t, HNodeRendererContext ctx, String s) {
        return ObjEx.of(ctx.computePropertyValue(t, s).orNull()).asDouble2();
    }

    public static NOptional<TsonElement> getTson(HNode t, HNodeRendererContext ctx, String s) {
        return ObjEx.of(ctx.computePropertyValue(t, s).orNull()).asTson();
    }

    public static NOptional<Double2> getDouble2OrHAlign(HNode t, HNodeRendererContext ctx, String s) {
        return ObjEx.of(ctx.computePropertyValue(t, s).orNull()).asDouble2OrHAlign();
    }

    public static NOptional<TsonNumber2> getTsonNumber2Or1OrHAlign(HNode t, HNodeRendererContext ctx, String s) {
        return ObjEx.of(ctx.computePropertyValue(t, s).orNull()).asTsonNumber2Or1OrHAlign();
    }

    public static NOptional<Double4> getDouble4(HNode t, HNodeRendererContext ctx, String s) {
        return ObjEx.of(ctx.computePropertyValue(t, s).orNull()).asDouble4();
    }

    public static NOptional<double[]> getDoubleArray(HNode t, HNodeRendererContext ctx, String s) {
        return ObjEx.of(ctx.computePropertyValue(t, s).orNull()).asDoubleArray();
    }

    public static NOptional<Padding> getPadding(HNode t, HNodeRendererContext ctx, String s) {
        return ObjEx.of(ctx.computePropertyValue(t, s).orNull()).asPadding();
    }

    public static NOptional<Rotation> getRotation(HNode t, HNodeRendererContext ctx, String s) {
        return ObjEx.of(ctx.computePropertyValue(t, s).orNull()).asRotation();
    }
}
