package net.thevpc.halfa.engin.spibase.renderer;

import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.renderer.HNodeRendererManager;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NAssert;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import net.thevpc.halfa.api.util.TsonUtils;
import net.thevpc.halfa.spi.eval.HNodeEval;

public abstract class HPartRendererContextImpl extends AbstractHNodeRendererContext {

    private HGraphics g3;
    private Bounds2 globalBound;
    private Bounds2 bound;
    private NSession session;
    private HMessageList messages;

    public HPartRendererContextImpl(HGraphics g, Dimension bound, Bounds2 globalBound, NSession session, HMessageList messages) {
        this.bound = new Bounds2(0, 0, bound.getWidth(), bound.getHeight());
        this.globalBound = globalBound;
        this.session = session;
        this.g3 = g;
        this.messages = messages;
    }
    public HPartRendererContextImpl(HGraphics g, Dimension bound, NSession session, HMessageList messages) {
        this(g, bound,new Bounds2(0, 0, bound.getWidth(), bound.getHeight()),session,messages);
    }

    @Override
    public HMessageList messages() {
        return messages;
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
    public <T> NOptional<T> computePropertyValue(HNode t, String s, String... others) {
        NAssert.requireNonBlank(s, "property name");
        NOptional<Object> r = computePropertyValueImpl(t, HUtils.uids(new String[]{s}, others));
        if (r.isPresent()) {
            Object y = r.get();
            HNodeEval ne = new HNodeEval(t);
            Object u = ne.eval(TsonUtils.toTson(y));
            if (u != null) {
                return NOptional.of((T) u);
            }
        }
        return (NOptional) r;
    }

    private <T> NOptional<T> computePropertyValueImpl(HNode t, String... all) {
        if (t != null) {
            for (String s : all) {
                NOptional<HProp> o = engine().computeProperty(t, s);
                if (o.isPresent()) {
                    NOptional<T> oo = o.map(HProp::getValue).map(x -> {
                        try {
                            return (T) x;
                        } catch (ClassCastException e) {
                            return null;
                        }
                    }).filter(x -> x != null);
                    if (oo.isPresent()) {
                        return oo;
                    }
                }
            }
        }
        if (all.length == 1) {
            return NOptional.ofNamedEmpty("style " + all[0]);
        }
        return NOptional.ofNamedEmpty("style one of " + Arrays.asList(all));
    }

    @Override
    public void render(HNode p, HNodeRendererContext ctx) {
        if (p.isTemplate()) {
            return;
        }
        manager().getRenderer(p.type()).get().render(p,ctx);
    }

    @Override
    public List<HProp> computeProperties(HNode t) {
        return engine().computeProperties(t);
    }

    @Override
    public HNodeRendererManager manager() {
        return engine().renderManager();
    }
}
