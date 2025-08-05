package net.thevpc.ntexup.engine.renderer;

import net.thevpc.ntexup.api.document.style.NTxProp;
import net.thevpc.ntexup.api.engine.NDocEngine;
import net.thevpc.ntexup.api.eval.NDocValueByName;
import net.thevpc.ntexup.api.eval.NDocVarProvider;
import net.thevpc.ntexup.api.log.NDocLogger;
import net.thevpc.ntexup.api.document.elem2d.NDocBounds2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.NDocNodeRenderer;
import net.thevpc.ntexup.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ntexup.api.util.NDocUtils;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererManager;
import net.thevpc.ntexup.engine.renderer.text.NutsHighlighterMapper;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.text.NText;
import net.thevpc.nuts.util.NAssert;
import net.thevpc.nuts.util.NLiteral;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class NDocNodeRendererContextBase extends NDocNodeRendererContextBaseBase {

    private NDocGraphics g3;
    private NDocEngine engine;
    private NDocBounds2 globalBound;
    private NDocBounds2 bound;
    private Map<String, Object> capabilities = new HashMap<>();
    private long pageStartTime;

    public NDocNodeRendererContextBase(NDocEngine engine, NDocGraphics g, Dimension bound, NDocBounds2 globalBound) {
        this.engine = engine;
        this.bound = new NDocBounds2(0, 0, bound.getWidth(), bound.getHeight());
        this.globalBound = globalBound;
        this.g3 = g;
    }

    public NDocNodeRendererContextBase(NDocEngine engine, NDocGraphics g, Dimension bound) {
        this(engine, g, bound, new NDocBounds2(0, 0, bound.getWidth(), bound.getHeight()));
    }

    public long getPageStartTime() {
        return pageStartTime;
    }

    public NDocNodeRendererContextBase setPageStartTime(long pageStartTime) {
        this.pageStartTime = pageStartTime;
        return this;
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
    public void highlightNutsText(String lang, String rawText, NText parsedText, NTxNode p, NDocTextRendererBuilder result) {
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
    public NOptional<NElement> computePropertyValue(NTxNode t, String s, String... others) {
        return computePropertyValue(t,s,others,null);
    }

    @Override
    public NOptional<NElement> computePropertyValue(NTxNode t, String s, String[] others, NDocVarProvider varProvider) {
        NAssert.requireNonBlank(s, "property name");
        NOptional<NElement> r = computePropertyValueImpl(t, NDocUtils.uids(new String[]{s}, others));
        if (r.isPresent()) {
            NElement y = r.get();
            y = engine().evalExpression(y, t, varProvider);
            if (y != null) {
                return NOptional.of(y);
            }
        }
        return r;
    }

    private NOptional<NElement> computePropertyValueImpl(NTxNode t, String... all) {
        if (t != null) {
            for (String s : all) {
                NOptional<NTxProp> o = engine().computeProperty(t, s);
                if (o.isPresent()) {
                    NOptional<NElement> oo = o.map(NTxProp::getValue).filter(x -> x != null);
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
    public void render(NTxNode p, NDocNodeRendererContext ctx) {
        NOptional<NDocNodeRenderer> renderer = manager().getRenderer(p.type());
        if (renderer.isPresent()) {
            renderer.get().render(p, ctx);
        } else {
            engine().log().log(NMsg.ofC("%s for %s",renderer.getMessage().get(),NDocUtils.snippet(p)).asError(),NDocUtils.sourceOf(p));
        }
    }

    @Override
    public List<NTxProp> computeProperties(NTxNode t) {
        return engine().computeProperties(t);
    }

    @Override
    public NDocNodeRendererManager manager() {
        return engine().renderManager();
    }

    @Override
    public boolean isDebug(NTxNode p) {
        return NDocValueByName.isDebug(p, this);
    }
}
