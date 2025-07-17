package net.thevpc.ndoc.spi.eval;

import net.thevpc.ndoc.api.model.HArrow;
import net.thevpc.ndoc.api.model.HArrowType;
import net.thevpc.ndoc.api.model.elem2d.*;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;

public class NDocValueByType {

    public static NOptional<HArrowType> getArrowType(NDocNode t, NDocNodeRendererContext ctx, String propName, String... propNames) {
        return NDocObjEx.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asArrowType();
    }

    public static NOptional<HArrow> getArrow(NDocNode t, NDocNodeRendererContext ctx, String propName, String... propNames) {
        return NDocObjEx.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asArrow();
    }

    public static NOptional<Boolean> getBoolean(NDocNode t, NDocNodeRendererContext ctx, String propName, String... propNames) {
        return NDocObjEx.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asBoolean();
    }

    public static NOptional<Integer> getIntOrBoolean(NDocNode t, NDocNodeRendererContext ctx, String propName, String... propNames) {
        return NDocObjEx.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asIntOrBoolean();
    }

    public static NOptional<Integer> getInt(NDocNode t, NDocNodeRendererContext ctx, String propName, String... propNames) {
        return NDocObjEx.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asInt();
    }

    public static NOptional<String> getStringOrName(NDocNode t, NDocNodeRendererContext ctx, String propName) {
        return NDocObjEx.of(ctx.computePropertyValue(t, propName).orNull()).asStringOrName();
    }

    public static NOptional<Paint> getPaint(NDocNode t, NDocNodeRendererContext ctx, String propName, String... propNames) {
        NDocObjEx r = NDocObjEx.of(ctx.computePropertyValue(t, propName, propNames).orElse(null));
        return NOptional.of(r.asPaint().orElse(null));
    }

    public static NOptional<Color> getColor(NDocNode t, NDocNodeRendererContext ctx, String propName, String... propNames) {
        NDocObjEx r = NDocObjEx.of(ctx.computePropertyValue(t, propName, propNames).orElse(null));
        return NOptional.of(r.asColor().orElse(null));
    }

    public static NOptional<Double> getDouble(NDocNode t, NDocNodeRendererContext ctx, String s) {
        return NDocObjEx.of(ctx.computePropertyValue(t, s).orNull()).asDouble();
    }

    public static NOptional<NDocDouble2> getDouble2(NDocNode t, NDocNodeRendererContext ctx, String s) {
        return NDocObjEx.of(ctx.computePropertyValue(t, s).orNull()).asDouble2();
    }

    public static NOptional<NElement> getTson(NDocNode t, NDocNodeRendererContext ctx, String s) {
        return NDocObjEx.of(ctx.computePropertyValue(t, s).orNull()).asTson();
    }

    public static NOptional<NDocDouble2> getDouble2OrHAlign(NDocNode t, NDocNodeRendererContext ctx, String s) {
        return NDocObjEx.of(ctx.computePropertyValue(t, s).orNull()).asDouble2OrHAlign();
    }

    public static NOptional<NDocElemNumber2> getNNumberElement2Or1OrHAlign(NDocNode t, NDocNodeRendererContext ctx, String s) {
        return NDocObjEx.of(ctx.computePropertyValue(t, s).orNull()).asNNumberElement2Or1OrHAlign();
    }

    public static NOptional<NDocDouble4> getDouble4(NDocNode t, NDocNodeRendererContext ctx, String s) {
        return NDocObjEx.of(ctx.computePropertyValue(t, s).orNull()).asDouble4();
    }

    public static NOptional<double[]> getDoubleArray(NDocNode t, NDocNodeRendererContext ctx, String s) {
        return NDocObjEx.of(ctx.computePropertyValue(t, s).orNull()).asDoubleArray();
    }

    public static NOptional<Padding> getPadding(NDocNode t, NDocNodeRendererContext ctx, String s) {
        return NDocObjEx.of(ctx.computePropertyValue(t, s).orNull()).asPadding();
    }

    public static NOptional<Rotation> getRotation(NDocNode t, NDocNodeRendererContext ctx, String s) {
        return NDocObjEx.of(ctx.computePropertyValue(t, s).orNull()).asRotation();
    }
}
