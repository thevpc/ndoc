package net.thevpc.ntexup.engine.renderer;

import net.thevpc.ntexup.api.document.style.NTxProp;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.eval.NDocVarProvider;
import net.thevpc.ntexup.api.log.NDocLogger;
import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ntexup.api.util.NDocUtils;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererManager;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.text.NText;
import net.thevpc.nuts.util.NAssert;
import net.thevpc.nuts.util.NOptional;

import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.List;


public class NDocNodeRendererContextDelegate extends NDocNodeRendererContextBaseBase {

    private NDocNodeRendererContext base;
    private NTxBounds2 bounds;
    private NTxNode basePart;
    private NTxProperties defaultStyles;
    private NDocGraphics graphics;
    private boolean dry;

    public NDocNodeRendererContextDelegate(NTxNode basePart,
                                           NDocNodeRendererContext base,
                                           NTxBounds2 bounds,
                                           NTxProperties defaultStyles,
                                           boolean dry,
                                           NDocGraphics graphics
    ) {
        this.basePart = basePart;
        this.base = base;
        this.bounds = bounds;
        this.defaultStyles = defaultStyles;
        this.dry = dry;
        this.graphics = graphics;
    }


    @Override
    public long getPageStartTime() {
        return base.getPageStartTime();
    }

    @Override
    public void highlightNutsText(String lang, String rawText, NText parsedText, NTxNode p, NDocTextRendererBuilder result) {
        base.highlightNutsText(lang, rawText, parsedText, p, result);
    }

    @Override
    public ImageObserver imageObserver() {
        return base.imageObserver();
    }

    @Override
    public NDocLogger log() {
        return base.log();
    }

    @Override
    public NDocNodeRendererManager manager() {
        return base.manager();
    }

    @Override
    public NDocNodeRendererContext withDefaultStyles(NTxNode node, NTxProperties defaultStyles) {
        return new NDocNodeRendererContextDelegate(node, base, bounds, defaultStyles == null ? this.defaultStyles : defaultStyles, dry, graphics);
    }

    @Override
    public NDocNodeRendererContext withBounds(NTxNode t, NTxBounds2 bounds2) {
        return new NDocNodeRendererContextDelegate(t, base, bounds2, defaultStyles, dry, graphics);
    }

    @Override
    public NDocNodeRendererContext withGraphics(NDocGraphics graphics) {
        return new NDocNodeRendererContextDelegate(basePart, base, bounds, defaultStyles, dry, graphics);
    }

    @Override
    public NDocNodeRendererContext dryMode() {
        return new NDocNodeRendererContextDelegate(basePart, base, bounds, defaultStyles, true, graphics);
    }

    @Override
    public boolean isDry() {
        return dry;
    }

    @Override
    public NDocGraphics graphics() {
        if (graphics != null) {
            return graphics;
        }
        return base.graphics();
    }

    @Override
    public NTxBounds2 getGlobalBounds() {
        return base.getGlobalBounds();
    }

    public NTxBounds2 getBounds() {
        if (bounds != null) {
            return bounds;
        }
        return base.getBounds();
    }

    @Override
    public void render(NTxNode p, NDocNodeRendererContext ctx) {
        base.render(p, ctx);
    }

    @Override
    public NTxEngine engine() {
        return base.engine();
    }

    @Override
    public List<NTxProp> computeProperties(NTxNode t) {
        List<NTxProp> inherited = engine().computeInheritedProperties(t);
        NTxProperties hp = new NTxProperties(t);
        if (this.defaultStyles != null) {
            hp.set(this.defaultStyles.toArray());
        }
        hp.set(t.getProperties());
        for (NTxProp h : inherited) {
            if (!hp.containsKey(h.getName())) {
                hp.set(h);
            }
        }
        return hp.toList();
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
        NOptional y = null;
        if (t != null) {
            y = engine().computeProperty(t, all).map(NTxProp::getValue).filter(x -> x != null);
            if (y.isPresent()) {
                return y;
            }
        }
        if (this.defaultStyles != null) {
            NOptional<NElement> u = this.defaultStyles.get(all).map(NTxProp::getValue).filter(x -> x != null);
            if (u.isPresent()) {
                return u;
            }
        }
        if (basePart != null) {
            for (String s : all) {
                y = basePart.getProperty(s).map(NTxProp::getValue).filter(x -> x != null);
                if (y.isPresent()) {
                    return y;
                }
            }
        }
        return base.computePropertyValue(null, all[0], Arrays.copyOfRange(all, 1, all.length));
    }

    @Override
    public boolean isAnimate() {
        return base.isAnimate();
    }

    @Override
    public void repaint() {
        base.repaint();
    }

    @Override
    public boolean isPrint() {
        return base.isPrint();
    }

    @Override
    public Object getCapability(String name) {
        return base.getCapability(name);
    }

    @Override
    public boolean hasCapability(String name) {
        return base.hasCapability(name);
    }

    @Override
    public boolean isCapability(String name) {
        return base.isCapability(name);
    }

    @Override
    public boolean isDebug(NTxNode p) {
        return base.isDebug(p);
    }
}
