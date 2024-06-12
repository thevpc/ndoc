package net.thevpc.halfa.engine.renderer.screen.common;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.renderer.HNodeRendererManager;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NOptional;

import java.util.List;

public class HPartRendererContextDelegate extends AbstractHNodeRendererContext {
    private HNodeRendererContext base;
    private Bounds2 bounds;
    private HNode basePart;
    private HProperties defaultStyles;
    private HGraphics graphics;
    private boolean dry;

    public HPartRendererContextDelegate(HNode basePart,
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
    public HNodeRendererManager manager() {
        return base.manager();
    }

    @Override
    public HNodeRendererContext withDefaultStyles(HNode node, HProperties defaultStyles) {
        return new HPartRendererContextDelegate(node, base, bounds, defaultStyles == null ? this.defaultStyles : defaultStyles, dry, graphics);
    }

    @Override
    public HNodeRendererContext withBounds(HNode t, Bounds2 bounds2) {
        return new HPartRendererContextDelegate(t, base, bounds2, defaultStyles, dry, graphics);
    }

    @Override
    public HNodeRendererContext withGraphics(HGraphics graphics) {
        return new HPartRendererContextDelegate(basePart, base, bounds, defaultStyles, dry, graphics);
    }

    @Override
    public HNodeRendererContext dryMode() {
        return new HPartRendererContextDelegate(basePart, base, bounds, defaultStyles, true, graphics);
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
    public <T> NOptional<T> computePropertyValue(HNode t, String s) {
        NOptional<T> y = null;
        if (t != null) {
            y = engine().computeProperty(t, s).map(HProp::getValue).map(x -> {
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
            NOptional<T> u = this.defaultStyles.get(s).map(HProp::getValue).map(x -> {
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
        return base.computePropertyValue(null, s);
    }
}
