package net.thevpc.ndoc.engine.renderer;

import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.engine.NDocLogger;
import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererManager;
import net.thevpc.ndoc.engine.renderer.text.NutsHighlighterMapper;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.text.NText;
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

public abstract class NDocNodeRendererContextBase extends NDocNodeRendererContextBaseBase {

    private NDocGraphics g3;
    private NDocEngine engine;
    private NDocBounds2 globalBound;
    private NDocBounds2 bound;
    private Map<String, Object> capabilities = new HashMap<>();

    public NDocNodeRendererContextBase(NDocEngine engine, NDocGraphics g, Dimension bound, NDocBounds2 globalBound) {
        this.engine=engine;
        this.bound = new NDocBounds2(0, 0, bound.getWidth(), bound.getHeight());
        this.globalBound = globalBound;
        this.g3 = g;
    }

    public NDocNodeRendererContextBase(NDocEngine engine, NDocGraphics g, Dimension bound) {
        this(engine,g, bound, new NDocBounds2(0, 0, bound.getWidth(), bound.getHeight()));
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
        return isCapability(CAPABILITY_ANIMATE);
    }

    @Override
    public boolean isPrint() {
        return isCapability(CAPABILITY_PRINT);
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
    public void highlightNutsText(String lang, String rawText, NText parsedText, NDocNode p, NDocTextRendererBuilder result) {
        NutsHighlighterMapper.highlightNutsText(lang, rawText, parsedText, p, this, result);
    }

    @Override
    public NDocLogger log() {
        return engine().log();
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
        NOptional<NElement> r = computePropertyValueImpl(t, NDocUtils.uids(new String[]{s}, others));
        if (r.isPresent()) {
            Object y = r.get();
            NElement u = engine.evalExpression(NElemUtils.toElement(y), t);
            if (u != null) {
                return NOptional.of(u);
            }
        }
        return r;
    }

    private NOptional<NElement> computePropertyValueImpl(NDocNode t, String... all) {
        if (t != null) {
            for (String s : all) {
                NOptional<NDocProp> o = engine().computeProperty(t, s);
                if (o.isPresent()) {
                    NOptional<NElement> oo = o.map(NDocProp::getValue).filter(x -> x != null);
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
    public List<NDocProp> computeProperties(NDocNode t) {
        return engine().computeProperties(t);
    }

    @Override
    public NDocNodeRendererManager manager() {
        return engine().renderManager();
    }
}
