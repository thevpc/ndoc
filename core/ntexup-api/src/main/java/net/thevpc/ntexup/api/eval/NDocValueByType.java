package net.thevpc.ntexup.api.eval;

import net.thevpc.ntexup.api.document.NDocArrow;
import net.thevpc.ntexup.api.document.NDocArrowType;
import net.thevpc.ntexup.api.document.elem2d.NDocDouble2;
import net.thevpc.ntexup.api.document.elem2d.*;
import net.thevpc.ntexup.api.document.node.*;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;

public class NDocValueByType {

    public static NOptional<NDocArrowType> getArrowType(NTxNode t, NDocNodeRendererContext ctx, String propName, String... propNames) {
        return NDocValue.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asArrowType();
    }

    public static NOptional<NDocArrow> getArrow(NTxNode t, NDocNodeRendererContext ctx, String propName, String... propNames) {
        return NDocValue.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asArrow();
    }

    public static NOptional<Boolean> getBoolean(NTxNode t, NDocNodeRendererContext ctx, String propName, String... propNames) {
        return NDocValue.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asBoolean();
    }

    public static NOptional<Integer> getIntOrBoolean(NTxNode t, NDocNodeRendererContext ctx, String propName, String... propNames) {
        return NDocValue.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asIntOrBoolean();
    }

    public static NOptional<Integer> getInt(NTxNode t, NDocNodeRendererContext ctx, String propName, String... propNames) {
        return NDocValue.of(ctx.computePropertyValue(t, propName, propNames).orNull()).asInt();
    }

    public static NOptional<String> getStringOrName(NTxNode t, NDocNodeRendererContext ctx, String propName) {
        return NDocValue.of(ctx.computePropertyValue(t, propName).orNull()).asStringOrName();
    }

    public static NOptional<Paint> getPaint(NTxNode t, NDocNodeRendererContext ctx, String propName, String... propNames) {
        NDocValue r = NDocValue.of(ctx.computePropertyValue(t, propName, propNames, ctx.varProvider()).orNull());
        return NOptional.of(r.asPaint().orNull());
    }

    public static NOptional<Color> getColor(NTxNode t, NDocNodeRendererContext ctx, String propName, String... propNames) {
        NDocValue r = NDocValue.of(ctx.computePropertyValue(t, propName, propNames).orNull());
        return NOptional.of(r.asColor().orNull());
    }

    public static NOptional<Double> getDouble(NTxNode t, NDocNodeRendererContext ctx, String s) {
        return NDocValue.of(ctx.computePropertyValue(t, s).orNull()).asDouble();
    }

    public static NOptional<NDocDouble2> getDouble2(NTxNode t, NDocNodeRendererContext ctx, String s) {
        return NDocValue.of(ctx.computePropertyValue(t, s).orNull()).asDouble2();
    }

    public static NOptional<NElement> getElement(NTxNode t, NDocNodeRendererContext ctx, String s) {
        return ctx.computePropertyValue(t, s);
    }

    public static NOptional<NDocDouble2> getDouble2OrHAlign(NTxNode t, NDocNodeRendererContext ctx, String s) {
        return NDocValue.of(ctx.computePropertyValue(t, s).orNull()).asDouble2OrHAlign();
    }

    public static NOptional<NDocElemNumber2> getNNumberElement2Or1OrHAlign(NTxNode t, NDocNodeRendererContext ctx, String s) {
        return NDocValue.of(ctx.computePropertyValue(t, s).orNull()).asNNumberElement2Or1OrHAlign();
    }

    public static NOptional<NDocDouble4> getDouble4(NTxNode t, NDocNodeRendererContext ctx, String s) {
        return NDocValue.of(ctx.computePropertyValue(t, s).orNull()).asDouble4();
    }

    public static NOptional<double[]> getDoubleArray(NTxNode t, NDocNodeRendererContext ctx, String s) {
        return NDocValue.of(ctx.computePropertyValue(t, s).orNull()).asDoubleArray();
    }

    public static NOptional<Padding> getPadding(NTxNode t, NDocNodeRendererContext ctx, String s) {
        return NDocValue.of(ctx.computePropertyValue(t, s).orNull()).asPadding();
    }

    public static NOptional<Rotation> getRotation(NTxNode t, NDocNodeRendererContext ctx, String s) {
        return NDocValue.of(ctx.computePropertyValue(t, s).orNull()).asRotation();
    }
}
