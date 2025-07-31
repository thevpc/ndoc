package net.thevpc.ndoc.engine.renderer;

import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.engine.NDocLogger;
import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererManager;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.text.NText;
import net.thevpc.nuts.util.NAssert;
import net.thevpc.nuts.util.NOptional;

import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.List;


public class NDocNodeRendererContextDelegate extends NDocNodeRendererContextBaseBase {

    private NDocNodeRendererContext base;
    private NDocBounds2 bounds;
    private NDocNode basePart;
    private NDocProperties defaultStyles;
    private NDocGraphics graphics;
    private boolean dry;

    public NDocNodeRendererContextDelegate(NDocNode basePart,
                                           NDocNodeRendererContext base,
                                           NDocBounds2 bounds,
                                           NDocProperties defaultStyles,
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
    public void highlightNutsText(String lang, String rawText, NText parsedText, NDocNode p, NDocTextRendererBuilder result) {
        base.highlightNutsText(lang,  rawText, parsedText, p, result);
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
    public NDocNodeRendererContext withDefaultStyles(NDocNode node, NDocProperties defaultStyles) {
        return new NDocNodeRendererContextDelegate(node, base, bounds, defaultStyles == null ? this.defaultStyles : defaultStyles, dry, graphics);
    }

    @Override
    public NDocNodeRendererContext withBounds(NDocNode t, NDocBounds2 bounds2) {
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
    public NDocBounds2 getGlobalBounds() {
        return base.getGlobalBounds();
    }

    public NDocBounds2 getBounds() {
        if (bounds != null) {
            return bounds;
        }
        return base.getBounds();
    }

    @Override
    public void render(NDocNode p, NDocNodeRendererContext ctx) {
        base.render(p, ctx);
    }

    @Override
    public NDocEngine engine() {
        return base.engine();
    }

    @Override
    public List<NDocProp> computeProperties(NDocNode t) {
        List<NDocProp> inherited = engine().computeInheritedProperties(t);
        NDocProperties hp = new NDocProperties(t);
        if (this.defaultStyles != null) {
            hp.set(this.defaultStyles.toArray());
        }
        hp.set(t.getProperties());
        for (NDocProp h : inherited) {
            if (!hp.containsKey(h.getName())) {
                hp.set(h);
            }
        }
        return hp.toList();
    }

    @Override
    public NOptional<NElement> computePropertyValue(NDocNode t, String s, String... others) {
        NAssert.requireNonBlank(s, "property name");
        NOptional<NElement> r = computePropertyValueImpl(t, NDocUtils.uids(new String[]{s}, others));
        if (r.isPresent()) {
            NElement y = r.get();
            y = engine().evalExpression(y, t);
            if (y != null) {
                return NOptional.of(y);
            }
        }
        return r;
    }

    private NOptional<NElement> computePropertyValueImpl(NDocNode t, String... all) {
        NOptional y = null;
        if (t != null) {
            y = engine().computeProperty(t, all).map(NDocProp::getValue).filter(x -> x != null);
            if (y.isPresent()) {
                return y;
            }
        }
        if (this.defaultStyles != null) {
            NOptional<NElement> u = this.defaultStyles.get(all).map(NDocProp::getValue).filter(x -> x != null);
            if (u.isPresent()) {
                return u;
            }
        }
        if (basePart != null) {
            for (String s : all) {
                y = basePart.getProperty(s).map(NDocProp::getValue).filter(x -> x != null);
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
}
