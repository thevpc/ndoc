package net.thevpc.ntexup.api.eval;

import net.thevpc.ntexup.api.document.NTxArrow;
import net.thevpc.ntexup.api.document.NTxArrowType;
import net.thevpc.ntexup.api.document.elem2d.NTxDouble2;
import net.thevpc.ntexup.api.document.elem2d.*;
import net.thevpc.ntexup.api.document.node.*;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;

public class NTxValueByType {

    public static NOptional<NTxArrowType> getArrowType(NTxNode t, NTxNodeRendererContext ctx, String propName, String... propNames) {
        return NTxValue.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asArrowType();
    }

    public static NOptional<NTxArrow> getArrow(NTxNode t, NTxNodeRendererContext ctx, String propName, String... propNames) {
        return NTxValue.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asArrow();
    }

    public static NOptional<Boolean> getBoolean(NTxNode t, NTxNodeRendererContext ctx, String propName, String... propNames) {
        return NTxValue.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asBoolean();
    }

    public static NOptional<Integer> getIntOrBoolean(NTxNode t, NTxNodeRendererContext ctx, String propName, String... propNames) {
        return NTxValue.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asIntOrBoolean();
    }

    public static NOptional<Integer> getInt(NTxNode t, NTxNodeRendererContext ctx, String propName, String... propNames) {
        return NTxValue.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asInt();
    }

    public static NOptional<String> getStringOrName(NTxNode t, NTxNodeRendererContext ctx, String propName) {
        return NTxValue.of(ctx.computePropertyValue(t, propName).orNull()).asStringOrName();
    }

    public static NOptional<Paint> getPaint(NTxNode t, NTxNodeRendererContext ctx, String propName, String... propNames) {
        NTxValue r = NTxValue.of(ctx.computePropertyValue(t, propName, propNames, ctx.varProvider()).orNull());
        return NOptional.of(r.asPaint().orNull());
    }

    public static NOptional<Color> getColor(NTxNode t, NTxNodeRendererContext ctx, String propName, String... propNames) {
        NTxValue r = NTxValue.of(ctx.computePropertyValue(t, propName, propNames).orNull());
        return NOptional.of(r.asColor().orNull());
    }

    public static NOptional<Double> getDouble(NTxNode t, NTxNodeRendererContext ctx, String s) {
        return NTxValue.of(ctx.computePropertyValue(t, s).orNull()).asDouble();
    }

    public static NOptional<NTxDouble2> getDouble2(NTxNode t, NTxNodeRendererContext ctx, String s) {
        return NTxValue.of(ctx.computePropertyValue(t, s).orNull()).asDouble2();
    }

    public static NOptional<NElement> getElement(NTxNode t, NTxNodeRendererContext ctx, String s) {
        return ctx.computePropertyValue(t, s);
    }

    public static NOptional<NTxDouble2> getDouble2OrHAlign(NTxNode t, NTxNodeRendererContext ctx, String s) {
        return NTxValue.of(ctx.computePropertyValue(t, s).orNull()).asDouble2OrHAlign();
    }

    public static NOptional<NTxElemNumber2> getNNumberElement2Or1OrHAlign(NTxNode t, NTxNodeRendererContext ctx, String s) {
        return NTxValue.of(ctx.computePropertyValue(t, s).orNull()).asNNumberElement2Or1OrHAlign();
    }

    public static NOptional<NTxDouble4> getDouble4(NTxNode t, NTxNodeRendererContext ctx, String s) {
        return NTxValue.of(ctx.computePropertyValue(t, s).orNull()).asDouble4();
    }

    public static NOptional<double[]> getDoubleArray(NTxNode t, NTxNodeRendererContext ctx, String s) {
        return NTxValue.of(ctx.computePropertyValue(t, s).orNull()).asDoubleArray();
    }

    public static NOptional<NTxMargin> getMargin(NTxNode t, NTxNodeRendererContext ctx, String s) {
        return NTxValue.of(ctx.computePropertyValue(t, s).orNull()).asPadding();
    }

    public static NOptional<NTxRotation> getRotation(NTxNode t, NTxNodeRendererContext ctx, String s) {
        return NTxValue.of(ctx.computePropertyValue(t, s).orNull()).asRotation();
    }
}
