package net.thevpc.halfa.engine.renderer.screen.common;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.api.style.HStyleType;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;

public abstract class HPartRendererContextImpl implements HPartRendererContext {
    private Graphics2D g;
    private Bounds2 globalBound;
    private Bounds2 bound;

    public HPartRendererContextImpl(Graphics2D g, Dimension bound, Bounds2 globalBound) {
        this.g = g;
        this.bound = new Bounds2(0,0, bound.getWidth(), bound.getHeight());
        this.globalBound = globalBound;
    }

    public Bounds2 getGlobalBounds() {
        return globalBound;
    }

    public Graphics2D getGraphics() {
        return g;
    }

    public Bounds2 getBounds() {
        return bound;
    }

    @Override
    public NOptional<HStyle> getStyle(HNode t, HStyleType s) {
        if(t!=null) {
            return t.computeStyle(s);
        }
        return NOptional.ofNamedEmpty("style "+s);
    }
}
