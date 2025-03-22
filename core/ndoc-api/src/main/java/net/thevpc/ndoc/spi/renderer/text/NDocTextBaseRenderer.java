package net.thevpc.ndoc.spi.renderer.text;

import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.elem2d.Double2;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.spi.model.NDocSizeRequirements;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.util.HNodeRendererUtils;
import net.thevpc.tson.TsonElement;

import java.util.*;
import java.util.stream.Collectors;

public abstract class NDocTextBaseRenderer extends NDocNodeRendererBase {


    protected HProperties defaultStyles = new HProperties();

    public NDocTextBaseRenderer(String... types) {
        super(types);
    }

    @Override
    public NDocSizeRequirements sizeRequirements(HNode p, NDocNodeRendererContext ctx) {
        Bounds2 s = selfBounds(p, ctx);
        Bounds2 bb = ctx.getBounds();
        return new NDocSizeRequirements(
                s.getWidth(),
                Math.max(bb.getWidth(), s.getWidth()),
                s.getWidth(),
                s.getHeight(),
                Math.max(bb.getHeight(), s.getHeight()),
                s.getHeight()
        );
    }

    public Bounds2 bgBounds(HNode p, NDocNodeRendererContext ctx) {
        return NDocValueByName.selfBounds(p, null, null, ctx);
    }

    public Bounds2 selfBounds(HNode p, NDocNodeRendererContext ctx) {
        NDocTextRendererBuilder helper = createRichTextHelper(p, ctx);
        Bounds2 bounds2 = helper.computeBound(ctx);
        return NDocValueByName.selfBounds(p, new Double2(bounds2.getWidth(), bounds2.getHeight()), null, ctx);
    }

    protected abstract NDocTextRendererBuilder createRichTextHelper(HNode p, NDocNodeRendererContext ctx);

    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        NDocGraphics g = ctx.graphics();
        HNodeRendererUtils.applyFont(p, g, ctx);


        NDocTextRendererBuilder helper = createRichTextHelper(p, ctx);

        Bounds2 bgBounds0 = bgBounds(p, ctx);
        Bounds2 bgBounds = bgBounds0;
        Bounds2 selfBounds = selfBounds(p, ctx);
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
                                    HPropName.SIZE,
                                    HPropName.ORIGIN,
                                    HPropName.POSITION
                            )
                            .stream().map(x
                                    -> p.getProperty(x).orNull()
                            ).filter(x -> x != null).collect(Collectors.toList())
                            + "\n"
                            + "eff: "
                            + Arrays.asList(
                                    HPropName.SIZE,
                                    HPropName.ORIGIN,
                                    HPropName.POSITION
                            )
                            .stream().map(x
                                            -> {
                                        TsonElement n = finalCtx.computePropertyValue(p, x).orNull();
                                        if (n == null) {
                                            return n;
                                        }
                                        return new HProp(x, n);
                                    }
                            ).filter(x -> x != null).collect(Collectors.toList())
                            + "\n",
                    30, 100
            );
        }

        helper.computeBound(ctx);
        helper.render(p, ctx, bgBounds, selfBounds);
        HNodeRendererUtils.paintDebugBox(p, ctx, g, selfBounds);
    }
}
