package net.thevpc.halfa.engine.renderer.screen.common;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.api.style.HStyleType;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.renderers.containers.HSizeRequirements;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HPartRendererContextDelegate implements HPartRendererContext {
    private HPartRendererContext base;
    private Rectangle2D.Double size;
    private HNode basePart;

    public HPartRendererContextDelegate(HNode basePart, HPartRendererContext base, Rectangle2D.Double size) {
        this.basePart = basePart;
        this.base = base;
        this.size = size;
    }

    @Override
    public Rectangle2D.Double getGlobalBounds() {
        return base.getGlobalBounds();
    }

    public Graphics2D getGraphics() {
        return base.getGraphics();
    }

    public Rectangle2D.Double getBounds() {
        return size;
    }

    @Override
    public Rectangle2D.Double paintPagePart(HNode p, HPartRendererContext ctx) {
        return base.paintPagePart(p, ctx);
    }

    @Override
    public HEngine engine() {
        return base.engine();
    }

    @Override
    public HDocumentFactory documentFactory() {
        return base.documentFactory();
    }

    @Override
    public HSizeRequirements computeSizeRequirements(HNode p, HPartRendererContext ctx) {
        return base.computeSizeRequirements(p, ctx);
    }

    @Override
    public NOptional<HStyle> getStyle(HNode t, HStyleType s) {
        NOptional<HStyle> y = null;
        if (t != null) {
            y = t.computeStyle(s);
            if (y.isPresent()) {
                return y;
            }
        }
        if (basePart != null) {
            y = basePart.getStyle(s);
            if (y.isPresent()) {
                return y;
            }
        }
        return base.getStyle(null, s);
    }
}
