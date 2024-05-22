package net.thevpc.halfa.engine.renderer.screen.common;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.api.style.HStyleType;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class HPartRendererContextImpl implements HPartRendererContext {
    private Graphics2D g;
    private Rectangle2D.Double globalBound;
    private Rectangle2D.Double bound;

    public HPartRendererContextImpl(Graphics2D g, Dimension bound, Rectangle2D.Double globalBound) {
        this.g = g;
        this.bound = new Rectangle2D.Double(0,0, bound.width, bound.height);
        this.globalBound = globalBound;
    }

    public Rectangle2D.Double getGlobalBounds() {
        return globalBound;
    }

    public Graphics2D getGraphics() {
        return g;
    }

    public Rectangle2D.Double getBounds() {
        return bound;
    }

    @Override
    public NOptional<HStyle> getStyle(HNode t, HStyleType s) {
        if(t!=null) {
            return t.getStyle(s);
        }
        return NOptional.ofNamedEmpty("style "+s);
    }
}
