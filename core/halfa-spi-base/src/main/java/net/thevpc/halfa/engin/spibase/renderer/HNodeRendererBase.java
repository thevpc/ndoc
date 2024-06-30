package net.thevpc.halfa.engin.spibase.renderer;

import net.thevpc.halfa.api.model.elem2d.*;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.eval.HValueByType;
import net.thevpc.halfa.spi.HNodeRenderer;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.eval.ObjEx;

public abstract class HNodeRendererBase implements HNodeRenderer {

    private String[] types;

    public HNodeRendererBase(String... types) {
        this.types = types;
    }

    @Override
    public HSizeRequirements sizeRequirements(HNode p, HNodeRendererContext ctx) {
        Bounds2 bounds = ctx.getBounds();
        return new HSizeRequirements(
                0,
                bounds.getWidth(),
                bounds.getWidth(),
                0,
                bounds.getHeight(),
                bounds.getHeight()
        );
    }

    @Override
    public String[] types() {
        return types;
    }

    public void render(HNode p, HNodeRendererContext ctx) {
        boolean v = HValueByName.isVisible(p, ctx);
        if (!v) {
            return;
        }
        Bounds2 selfBounds = selfBounds(p, ctx);
        HGraphics nv = null;
        try {
            if (!ctx.isDry()) {
                Rotation rotation = HValueByType.getRotation(p, ctx, HPropName.ROTATE).orNull();
                if (rotation != null) {
                    double angle = ObjEx.of(rotation.getAngle()).asDouble().orElse(0.0);
                    if (angle != 0) {
                        angle = angle / 180.0 * Math.PI;
                        if (angle != 0) {
                            HGraphics g = ctx.graphics();
                            nv = g.copy();
                            ///HSizeRef sr=new HSizeRef();
                            double rotX = ObjEx.of(rotation.getX()).asDouble().get() / 100.0 * selfBounds.getWidth() + selfBounds.getX();
                            double rotY = ObjEx.of(rotation.getY()).asDouble().get() / 100.0 * selfBounds.getHeight() + selfBounds.getY();
                            if (HValueByName.isDebug(p, ctx)) {
                                g.setColor(HValueByName.getDebugColor(p, ctx));
                                g.drawRect(selfBounds);
                                g.fillRect(rotX - 3, rotY - 3, 6, 6);
//                            g.drawString(rotX + "," + rotY+" : "+p, 100, 100);
                            }
                            nv.rotate(
                                    angle,
                                    rotX,
                                    rotY
                            );
                            ctx = ctx.withGraphics(nv);
                        }
                    }
                }
            }
            renderMain(p, ctx);
        } finally {
            if (nv != null) {
                nv.dispose();
            }
        }
    }

    public abstract void renderMain(HNode p, HNodeRendererContext ctx);

    public Bounds2 bgBounds(HNode p, HNodeRendererContext ctx) {
        return HValueByName.selfBounds(p, null, null, ctx);
    }

    public Bounds2 selfBounds(HNode t, HNodeRendererContext ctx) {
        return HValueByName.selfBounds(t, null, null, ctx);
    }

}
