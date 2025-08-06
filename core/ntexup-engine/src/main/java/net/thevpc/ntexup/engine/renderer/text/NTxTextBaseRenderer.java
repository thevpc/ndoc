package net.thevpc.ntexup.engine.renderer.text;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.elem2d.NTxDouble2;
import net.thevpc.ntexup.api.document.node.*;
import net.thevpc.ntexup.api.document.style.*;
import net.thevpc.ntexup.api.document.NTxSizeRequirements;
import net.thevpc.ntexup.api.eval.NTxValueByName;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.api.renderer.text.NTxTextRendererBuilder;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.engine.util.NTxNodeRendererUtils;
import net.thevpc.ntexup.engine.renderer.NTxNodeRendererBase;
import net.thevpc.nuts.elem.NElement;

import java.util.*;
import java.util.stream.Collectors;

public abstract class NTxTextBaseRenderer extends NTxNodeRendererBase {


    protected NTxProperties defaultStyles = new NTxProperties(null);

    public NTxTextBaseRenderer(String... types) {
        super(types);
    }

    @Override
    public NTxSizeRequirements sizeRequirements(NTxNode p, NTxNodeRendererContext ctx) {
        NTxBounds2 s = selfBounds(p, ctx);
        NTxBounds2 bb = ctx.getBounds();
        return new NTxSizeRequirements(
                s.getWidth(),
                Math.max(bb.getWidth(), s.getWidth()),
                s.getWidth(),
                s.getHeight(),
                Math.max(bb.getHeight(), s.getHeight()),
                s.getHeight()
        );
    }

    public NTxBounds2 bgBounds(NTxNode p, NTxNodeRendererContext ctx) {
        return ctx.selfBounds(p, null, null);
    }

    public NTxBounds2 selfBounds(NTxNode p, NTxNodeRendererContext ctx) {
        return defaultSelfBounds(p, ctx);
    }

    public NTxBounds2 defaultSelfBounds(NTxNode p, NTxNodeRendererContext ctx) {
        NTxTextRendererBuilder helper = createRichTextHelper(p, ctx);
        NTxBounds2 bounds2 = helper.computeBound(ctx);
        return NTxValueByName.selfBounds(p, new NTxDouble2(bounds2.getWidth(), bounds2.getHeight()), null, ctx);
    }

    protected abstract NTxTextRendererBuilder createRichTextHelper(NTxNode p, NTxNodeRendererContext ctx);

    public void renderMain(NTxNode p, NTxNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NTxGraphics g = ctx.graphics();
        NTxNodeRendererUtils.applyFont(p, g, ctx);


        NTxTextRendererBuilder helper = createRichTextHelper(p, ctx);

        NTxBounds2 bgBounds0 = bgBounds(p, ctx);
        NTxBounds2 bgBounds = bgBounds0;
        NTxBounds2 selfBounds = selfBounds(p, ctx);
        bgBounds = bgBounds.expand(selfBounds);

        NTxNodeRendererContext finalCtx = ctx;
        if (NTxValueByName.getDebugLevel(p, ctx) >= 10) {
            g.debugString(
                    "Plain:\n"
                            + "expected=" + bgBounds0 + "\n"
                            + "fullSize=" + selfBounds + "\n"
                            + "newExpectedBounds=" + bgBounds + "\n"
                            + "curr: "
                            + Arrays.asList(
                                    NTxPropName.SIZE,
                                    NTxPropName.ORIGIN,
                                    NTxPropName.POSITION
                            )
                            .stream().map(x
                                    -> p.getProperty(x).orNull()
                            ).filter(x -> x != null).collect(Collectors.toList())
                            + "\n"
                            + "eff: "
                            + Arrays.asList(
                                    NTxPropName.SIZE,
                                    NTxPropName.ORIGIN,
                                    NTxPropName.POSITION
                            )
                            .stream().map(x
                                            -> {
                                        NElement n = finalCtx.computePropertyValue(p, x).orNull();
                                        if (n == null) {
                                            return n;
                                        }
                                        return new NTxProp(x, n, p);
                                    }
                            ).filter(x -> x != null).collect(Collectors.toList())
                            + "\n",
                    30, 100
            );
        }

        helper.computeBound(ctx);
        helper.render(p, ctx, bgBounds, selfBounds);
        NTxNodeRendererUtils.paintDebugBox(p, ctx, g, selfBounds);
    }
}
