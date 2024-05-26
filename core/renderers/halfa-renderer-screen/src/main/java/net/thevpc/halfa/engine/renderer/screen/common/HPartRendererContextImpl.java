package net.thevpc.halfa.engine.renderer.screen.common;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.renderers.HGraphicsImpl;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;

public abstract class HPartRendererContextImpl extends AbstractHNodeRendererContext {
    private HGraphics g3;
    private Bounds2 globalBound;
    private Bounds2 bound;
    private NSession session;

    public HPartRendererContextImpl(Graphics2D g, Dimension bound, Bounds2 globalBound, NSession session) {
        this.bound = new Bounds2(0, 0, bound.getWidth(), bound.getHeight());
        this.globalBound = globalBound;
        this.session = session;
        this.g3 = new HGraphicsImpl(g);
    }

    @Override
    public boolean isDry() {
        return false;
    }



    @Override
    public NSession session() {
        return session;
    }

    @Override
    public HGraphics graphics() {
        return g3;
    }

    public Bounds2 getGlobalBounds() {
        return globalBound;
    }

    public Bounds2 getBounds() {
        return bound;
    }

    @Override
    public <T> NOptional<T> getProperty(HNode t, String s) {
        if (t != null) {
            return t.computeProperty(s).map(HProp::getValue).map(x->{
                try {
                    return (T) x;
                }catch (ClassCastException e){
                    return null;
                }
            }).filter(x->x!=null);
        }
        return NOptional.ofNamedEmpty("style " + s);
    }
}
