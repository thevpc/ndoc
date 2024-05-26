package net.thevpc.halfa.engine.renderer.screen.common;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.model.HSizeRequirements;

public abstract class ConvertedHPartRenderer extends AbstractHNodeRenderer {
    public ConvertedHPartRenderer(String... types) {
        super(types);
    }

    public abstract HNode convert(HNode p, HNodeRendererContext ctx) ;

    private HNode convertAndInherit(HNode p, HNodeRendererContext ctx) {
        HNode t = convert(p, ctx);
        //just to inherit rules and so on...
        t.setParent(p);
        return t;
    }

    public HSizeRequirements render(HNode p, HNodeRendererContext ctx) {
        return ctx.render(convertAndInherit(p, ctx));
    }

}
