package net.thevpc.ndoc.spi.base.renderer;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.HLogger;
import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererManager;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NAssert;
import net.thevpc.nuts.util.NOptional;

import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.List;

import net.thevpc.ndoc.spi.eval.NDocNodeEvalNDoc;
import net.thevpc.nuts.elem.NElement;

public class NDocNodeRendererContextDelegate extends NDocNodeRendererContextBaseBase {

    private NDocNodeRendererContext base;
    private Bounds2 bounds;
    private HNode basePart;
    private HProperties defaultStyles;
    private NDocGraphics graphics;
    private boolean dry;

    public NDocNodeRendererContextDelegate(HNode basePart,
                                           NDocNodeRendererContext base,
                                           Bounds2 bounds,
                                           HProperties defaultStyles,
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
    public ImageObserver imageObserver() {
        return base.imageObserver();
    }

    @Override
    public HLogger log() {
        return base.log();
    }

    @Override
    public NDocNodeRendererManager manager() {
        return base.manager();
    }

    @Override
    public NDocNodeRendererContext withDefaultStyles(HNode node, HProperties defaultStyles) {
        return new NDocNodeRendererContextDelegate(node, base, bounds, defaultStyles == null ? this.defaultStyles : defaultStyles, dry, graphics);
    }

    @Override
    public NDocNodeRendererContext withBounds(HNode t, Bounds2 bounds2) {
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
    public Bounds2 getGlobalBounds() {
        return base.getGlobalBounds();
    }

    public Bounds2 getBounds() {
        if (bounds != null) {
            return bounds;
        }
        return base.getBounds();
    }

    @Override
    public void render(HNode p, NDocNodeRendererContext ctx) {
        base.render(p, ctx);
    }

    @Override
    public NDocEngine engine() {
        return base.engine();
    }

    @Override
    public List<HProp> computeProperties(HNode t) {
        List<HProp> inherited = engine().computeInheritedProperties(t);
        HProperties hp = new HProperties();
        if (this.defaultStyles != null) {
            hp.set(this.defaultStyles.toArray());
        }
        hp.set(t.getProperties());
        for (HProp h : inherited) {
            if (!hp.containsKey(h.getName())) {
                hp.set(h);
            }
        }
        return hp.toList();
    }

    @Override
    public NOptional<NElement> computePropertyValue(HNode t, String s, String... others) {
        NAssert.requireNonBlank(s, "property name");
        NOptional<NElement> r = computePropertyValueImpl(t, HUtils.uids(new String[]{s}, others));
        if (r.isPresent()) {
            NElement y = r.get();
            NDocNodeEvalNDoc ne = new NDocNodeEvalNDoc(t);
            y = ne.eval(y);
            if (y != null) {
                return NOptional.of(y);
            }
        }
        return (NOptional) r;
    }

    private NOptional<NElement> computePropertyValueImpl(HNode t, String... all) {
        NOptional y = null;
        if (t != null) {
            y = engine().computeProperty(t, all).map(HProp::getValue).filter(x -> x != null);
            if (y.isPresent()) {
                return y;
            }
        }
        if (this.defaultStyles != null) {
            NOptional<NElement> u = this.defaultStyles.get(all).map(HProp::getValue).filter(x -> x != null);
            if (u.isPresent()) {
                return u;
            }
        }
        if (basePart != null) {
            for (String s : all) {
                y = basePart.getProperty(s).map(HProp::getValue).filter(x -> x != null);
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
