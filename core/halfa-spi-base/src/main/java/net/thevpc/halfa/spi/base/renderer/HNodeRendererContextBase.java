package net.thevpc.halfa.spi.base.renderer;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.renderer.HNodeRendererManager;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NAssert;
import net.thevpc.nuts.util.NLiteral;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.thevpc.halfa.api.util.TsonUtils;
import net.thevpc.halfa.spi.eval.HNodeEval;
import net.thevpc.tson.TsonElement;

public abstract class HNodeRendererContextBase extends HNodeRendererContextBaseBase {

    private HGraphics g3;
    private HEngine engine;
    private Bounds2 globalBound;
    private Bounds2 bound;
    private NSession session;
    private HMessageList messages;
    private Map<String, Object> capabilities = new HashMap<>();

    public HNodeRendererContextBase(HEngine engine,HGraphics g, Dimension bound, Bounds2 globalBound, NSession session, HMessageList messages) {
        this.engine=engine;
        this.bound = new Bounds2(0, 0, bound.getWidth(), bound.getHeight());
        this.globalBound = globalBound;
        this.session = session;
        this.g3 = g;
        this.messages = messages;
    }

    public HNodeRendererContextBase(HEngine engine,HGraphics g, Dimension bound, NSession session, HMessageList messages) {
        this(engine,g, bound, new Bounds2(0, 0, bound.getWidth(), bound.getHeight()), session, messages);
    }

    @Override
    public ImageObserver imageObserver() {
        return null;
    }

    @Override
    public void repaint() {

    }

    @Override
    public HEngine engine() {
        return engine;
    }


    public void setCapability(String name, Object value) {
        if (value == null) {
            capabilities.remove(name);
        } else {
            capabilities.put(name, value);
        }
    }

    @Override
    public boolean isAnimate() {
        return isCapability(HNodeRendererContext.CAPABILITY_ANIMATE);
    }

    @Override
    public boolean isPrint() {
        return isCapability(HNodeRendererContext.CAPABILITY_PRINT);
    }

    public Object getCapability(String name) {
        return capabilities.get(name);
    }

    public boolean hasCapability(String name) {
        return capabilities.get(name) != null;
    }

    public boolean isCapability(String name) {
        return NLiteral.of(getCapability(name))
                .asBoolean().orElse(false);
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
    public NOptional<TsonElement> computePropertyValue(HNode t, String s, String... others) {
        NAssert.requireNonBlank(s, "property name");
        NOptional<TsonElement> r = computePropertyValueImpl(t, HUtils.uids(new String[]{s}, others));
        if (r.isPresent()) {
            Object y = r.get();
            HNodeEval ne = new HNodeEval(t);
            TsonElement u = ne.eval(TsonUtils.toTson(y));
            if (u != null) {
                return NOptional.of(u);
            }
        }
        return (NOptional) r;
    }

    private NOptional<TsonElement> computePropertyValueImpl(HNode t, String... all) {
        if (t != null) {
            for (String s : all) {
                NOptional<HProp> o = engine().computeProperty(t, s);
                if (o.isPresent()) {
                    NOptional<TsonElement> oo = o.map(HProp::getValue).filter(x -> x != null);
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
        manager().getRenderer(p.type()).get().render(p, ctx);
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
