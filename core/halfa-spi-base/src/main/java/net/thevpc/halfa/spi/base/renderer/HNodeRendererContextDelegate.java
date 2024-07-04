package net.thevpc.halfa.spi.base.renderer;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.renderer.HNodeRendererManager;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NAssert;
import net.thevpc.nuts.util.NOptional;

import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.List;

import net.thevpc.halfa.api.util.TsonUtils;
import net.thevpc.halfa.spi.eval.HNodeEval;
import net.thevpc.tson.TsonElementBase;

public class HNodeRendererContextDelegate extends HNodeRendererContextBaseBase {

    private HNodeRendererContext base;
    private Bounds2 bounds;
    private HNode basePart;
    private HProperties defaultStyles;
    private HGraphics graphics;
    private boolean dry;

    public HNodeRendererContextDelegate(HNode basePart,
                                        HNodeRendererContext base,
                                        Bounds2 bounds,
                                        HProperties defaultStyles,
                                        boolean dry,
                                        HGraphics graphics
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
    public HMessageList messages() {
        return base.messages();
    }

    @Override
    public HNodeRendererManager manager() {
        return base.manager();
    }

    @Override
    public HNodeRendererContext withDefaultStyles(HNode node, HProperties defaultStyles) {
        return new HNodeRendererContextDelegate(node, base, bounds, defaultStyles == null ? this.defaultStyles : defaultStyles, dry, graphics);
    }

    @Override
    public HNodeRendererContext withBounds(HNode t, Bounds2 bounds2) {
        return new HNodeRendererContextDelegate(t, base, bounds2, defaultStyles, dry, graphics);
    }

    @Override
    public HNodeRendererContext withGraphics(HGraphics graphics) {
        return new HNodeRendererContextDelegate(basePart, base, bounds, defaultStyles, dry, graphics);
    }

    @Override
    public HNodeRendererContext dryMode() {
        return new HNodeRendererContextDelegate(basePart, base, bounds, defaultStyles, true, graphics);
    }

    @Override
    public boolean isDry() {
        return dry;
    }

    @Override
    public NSession session() {
        return base.session();
    }

    @Override
    public HGraphics graphics() {
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
    public void render(HNode p, HNodeRendererContext ctx) {
        base.render(p, ctx);
    }

    @Override
    public HEngine engine() {
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
    public <T> NOptional<T> computePropertyValue(HNode t, String s, String... others) {
        NAssert.requireNonBlank(s, "property name");
        NOptional<Object> r = computePropertyValueImpl(t, HUtils.uids(new String[]{s}, others));
        if (r.isPresent()) {
            Object y = r.get();
            HNodeEval ne = new HNodeEval(t);
            if (y instanceof TsonElementBase || y instanceof String) {
                y = ne.eval(TsonUtils.toTson(y));
            }
            if (y != null) {
                return NOptional.of((T) y);
            }
        }
        return (NOptional) r;
    }

    private <T> NOptional<T> computePropertyValueImpl(HNode t, String... all) {
        NOptional<T> y = null;
        if (t != null) {
            y = engine().computeProperty(t, all).map(HProp::getValue).map(x -> {
                try {
                    return (T) x;
                } catch (ClassCastException e) {
                    return null;
                }
            }).filter(x -> x != null);
            if (y.isPresent()) {
                return y;
            }
        }
        if (this.defaultStyles != null) {
            NOptional<T> u = this.defaultStyles.get(all).map(HProp::getValue).map(x -> {
                try {
                    return (T) x;
                } catch (ClassCastException e) {
                    return null;
                }
            }).filter(x -> x != null);
            if (u.isPresent()) {
                return u;
            }
        }
        if (basePart != null) {
            for (String s : all) {
                y = basePart.getProperty(s).map(HProp::getValue).map(x -> {
                    try {
                        return (T) x;
                    } catch (ClassCastException e) {
                        return null;
                    }
                }).filter(x -> x != null);
                if (y.isPresent()) {
                    return y;
                }
            }
        }
        return base.computePropertyValue(null, all[0], Arrays.copyOfRange(all, 1, all.length));
    }

    @Override
    public boolean isAnimated() {
        return base.isAnimated();
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
