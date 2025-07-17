package net.thevpc.ndoc.spi.base.renderer;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.NDocLogger;
import net.thevpc.ndoc.api.model.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererManager;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NAssert;
import net.thevpc.nuts.util.NLiteral;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.thevpc.ndoc.api.util.NElemUtils;
import net.thevpc.ndoc.spi.eval.NDocNodeEvalNDoc;

public abstract class NDocNodeRendererContextBase extends NDocNodeRendererContextBaseBase {

    private NDocGraphics g3;
    private NDocEngine engine;
    private NDocBounds2 globalBound;
    private NDocBounds2 bound;
    private NDocLogger log;
    private Map<String, Object> capabilities = new HashMap<>();

    public NDocNodeRendererContextBase(NDocEngine engine, NDocGraphics g, Dimension bound, NDocBounds2 globalBound, NDocLogger log) {
        this.engine=engine;
        this.bound = new NDocBounds2(0, 0, bound.getWidth(), bound.getHeight());
        this.globalBound = globalBound;
        this.g3 = g;
        this.log = log;
    }

    public NDocNodeRendererContextBase(NDocEngine engine, NDocGraphics g, Dimension bound, NDocLogger log) {
        this(engine,g, bound, new NDocBounds2(0, 0, bound.getWidth(), bound.getHeight()), log);
    }

    @Override
    public ImageObserver imageObserver() {
        return null;
    }

    @Override
    public void repaint() {

    }

    @Override
    public NDocEngine engine() {
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
        return isCapability(NDocNodeRendererContext.CAPABILITY_ANIMATE);
    }

    @Override
    public boolean isPrint() {
        return isCapability(NDocNodeRendererContext.CAPABILITY_PRINT);
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
    public NDocLogger log() {
        return log;
    }

    @Override
    public boolean isDry() {
        return false;
    }

    @Override
    public NDocGraphics graphics() {
        return g3;
    }

    public NDocBounds2 getGlobalBounds() {
        return globalBound;
    }

    public NDocBounds2 getBounds() {
        return bound;
    }

    @Override
    public NOptional<NElement> computePropertyValue(NDocNode t, String s, String... others) {
        NAssert.requireNonBlank(s, "property name");
        NOptional<NElement> r = computePropertyValueImpl(t, HUtils.uids(new String[]{s}, others));
        if (r.isPresent()) {
            Object y = r.get();
            NDocNodeEvalNDoc ne = new NDocNodeEvalNDoc(t);
            NElement u = ne.eval(NElemUtils.toElement(y));
            if (u != null) {
                return NOptional.of(u);
            }
        }
        return (NOptional) r;
    }

    private NOptional<NElement> computePropertyValueImpl(NDocNode t, String... all) {
        if (t != null) {
            for (String s : all) {
                NOptional<HProp> o = engine().computeProperty(t, s);
                if (o.isPresent()) {
                    NOptional<NElement> oo = o.map(HProp::getValue).filter(x -> x != null);
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
    public void render(NDocNode p, NDocNodeRendererContext ctx) {
        manager().getRenderer(p.type()).get().render(p, ctx);
    }

    @Override
    public List<HProp> computeProperties(NDocNode t) {
        return engine().computeProperties(t);
    }

    @Override
    public NDocNodeRendererManager manager() {
        return engine().renderManager();
    }
}
