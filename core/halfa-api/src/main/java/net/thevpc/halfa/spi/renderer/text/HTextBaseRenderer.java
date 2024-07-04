package net.thevpc.halfa.spi.renderer.text;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.Double2;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererBase;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.HNodeRendererUtils;

import java.util.*;
import java.util.stream.Collectors;

public abstract class HTextBaseRenderer extends HNodeRendererBase {


    protected HProperties defaultStyles = new HProperties();

    public HTextBaseRenderer(String... types) {
        super(types);
    }

    @Override
    public HSizeRequirements sizeRequirements(HNode p, HNodeRendererContext ctx) {
        Bounds2 s = selfBounds(p, ctx);
        Bounds2 bb = ctx.getBounds();
        return new HSizeRequirements(
                s.getWidth(),
                Math.max(bb.getWidth(), s.getWidth()),
                s.getWidth(),
                s.getHeight(),
                Math.max(bb.getHeight(), s.getHeight()),
                s.getHeight()
        );
    }

    public Bounds2 bgBounds(HNode p, HNodeRendererContext ctx) {
        return HValueByName.selfBounds(p, null, null, ctx);
    }

    public Bounds2 selfBounds(HNode p, HNodeRendererContext ctx) {
        HTextRendererBuilder helper = createRichTextHelper(p, ctx);
        Bounds2 bounds2 = helper.computeBound(ctx);
        return HValueByName.selfBounds(p, new Double2(bounds2.getWidth(), bounds2.getHeight()), null, ctx);
    }

    protected abstract HTextRendererBuilder createRichTextHelper(HNode p, HNodeRendererContext ctx);

    public void renderMain(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        HGraphics g = ctx.graphics();
        HNodeRendererUtils.applyFont(p, g, ctx);


        HTextRendererBuilder helper = createRichTextHelper(p, ctx);

        Bounds2 bgBounds0 = bgBounds(p, ctx);
        Bounds2 bgBounds = bgBounds0;
        Bounds2 selfBounds = selfBounds(p, ctx);
        bgBounds = bgBounds.expand(selfBounds);

        HNodeRendererContext finalCtx = ctx;
        if (HValueByName.getDebugLevel(p, ctx) >= 10) {
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
                                        Object n = finalCtx.computePropertyValue(p, x).orNull();
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
