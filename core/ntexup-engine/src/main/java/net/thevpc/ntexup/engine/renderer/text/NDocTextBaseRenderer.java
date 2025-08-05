package net.thevpc.ntexup.engine.renderer.text;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.elem2d.NTxDouble2;
import net.thevpc.ntexup.api.document.node.*;
import net.thevpc.ntexup.api.document.style.*;
import net.thevpc.ntexup.api.document.NDocSizeRequirements;
import net.thevpc.ntexup.api.eval.NDocValueByName;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.ntexup.engine.util.NDocNodeRendererUtils;
import net.thevpc.ntexup.engine.renderer.NDocNodeRendererBase;
import net.thevpc.nuts.elem.NElement;

import java.util.*;
import java.util.stream.Collectors;

public abstract class NDocTextBaseRenderer extends NDocNodeRendererBase {


    protected NTxProperties defaultStyles = new NTxProperties(null);

    public NDocTextBaseRenderer(String... types) {
        super(types);
    }

    @Override
    public NDocSizeRequirements sizeRequirements(NTxNode p, NDocNodeRendererContext ctx) {
        NTxBounds2 s = selfBounds(p, ctx);
        NTxBounds2 bb = ctx.getBounds();
        return new NDocSizeRequirements(
                s.getWidth(),
                Math.max(bb.getWidth(), s.getWidth()),
                s.getWidth(),
                s.getHeight(),
                Math.max(bb.getHeight(), s.getHeight()),
                s.getHeight()
        );
    }

    public NTxBounds2 bgBounds(NTxNode p, NDocNodeRendererContext ctx) {
        return ctx.selfBounds(p, null, null);
    }

    public NTxBounds2 selfBounds(NTxNode p, NDocNodeRendererContext ctx) {
        NDocTextRendererBuilder helper = createRichTextHelper(p, ctx);
        NTxBounds2 bounds2 = helper.computeBound(ctx);
        return NDocValueByName.selfBounds(p, new NTxDouble2(bounds2.getWidth(), bounds2.getHeight()), null, ctx);
    }

    public NTxBounds2 defaultSelfBounds(NTxNode p, NDocNodeRendererContext ctx) {
        NDocTextRendererBuilder helper = createRichTextHelper(p, ctx);
        NTxBounds2 bounds2 = helper.computeBound(ctx);
        return NDocValueByName.selfBounds(p, new NTxDouble2(bounds2.getWidth(), bounds2.getHeight()), null, ctx);
    }

    protected abstract NDocTextRendererBuilder createRichTextHelper(NTxNode p, NDocNodeRendererContext ctx);

    public void renderMain(NTxNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocGraphics g = ctx.graphics();
        NDocNodeRendererUtils.applyFont(p, g, ctx);


        NDocTextRendererBuilder helper = createRichTextHelper(p, ctx);

        NTxBounds2 bgBounds0 = bgBounds(p, ctx);
        NTxBounds2 bgBounds = bgBounds0;
        NTxBounds2 selfBounds = selfBounds(p, ctx);
        bgBounds = bgBounds.expand(selfBounds);

        NDocNodeRendererContext finalCtx = ctx;
        if (NDocValueByName.getDebugLevel(p, ctx) >= 10) {
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
        NDocNodeRendererUtils.paintDebugBox(p, ctx, g, selfBounds);
    }
}
