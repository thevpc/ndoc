package net.thevpc.ndoc.spi.renderer;

import net.thevpc.ndoc.api.model.elem2d.*;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.eval.NDocValueByType;
import net.thevpc.ndoc.spi.NDocNodeRenderer;
import net.thevpc.ndoc.spi.model.NDocSizeRequirements;
import net.thevpc.ndoc.spi.eval.NDocObjEx;

public abstract class NDocNodeRendererBase implements NDocNodeRenderer {

    private String[] types;

    public NDocNodeRendererBase(String... types) {
        this.types = types;
    }

    @Override
    public NDocSizeRequirements sizeRequirements(HNode p, NDocNodeRendererContext ctx) {
        Bounds2 bounds = ctx.getBounds();
        return new NDocSizeRequirements(
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

    public void render(HNode p, NDocNodeRendererContext ctx) {
        boolean v = NDocValueByName.isVisible(p, ctx);
        if (!v) {
            return;
        }
        Bounds2 selfBounds = selfBounds(p, ctx);
        NDocGraphics nv = null;
        try {
            if (!ctx.isDry()) {
            //if (!ctx.isDry()) {
                Rotation rotation = NDocValueByType.getRotation(p, ctx, HPropName.ROTATE).orNull();
                if (rotation != null) {
                    double angle = NDocObjEx.of(rotation.getAngle()).asDouble().orElse(0.0);
                    if (angle != 0) {
                        angle = angle / 180.0 * Math.PI;
                        if (angle != 0) {
                            NDocGraphics g = ctx.graphics();
                            nv = g.copy();
                            ///HSizeRef sr=new HSizeRef();
                            double rotX = NDocObjEx.of(rotation.getX()).asDouble().get() / 100.0 * selfBounds.getWidth() + selfBounds.getX();
                            double rotY = NDocObjEx.of(rotation.getY()).asDouble().get() / 100.0 * selfBounds.getHeight() + selfBounds.getY();
                            if (NDocValueByName.isDebug(p, ctx)) {
                                g.setColor(NDocValueByName.getDebugColor(p, ctx));
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
            //}
                renderMain(p, ctx);
            }
        } finally {
            if (nv != null) {
                nv.dispose();
            }
        }
    }

    public abstract void renderMain(HNode p, NDocNodeRendererContext ctx);

    public Bounds2 bgBounds(HNode p, NDocNodeRendererContext ctx) {
        return NDocValueByName.selfBounds(p, null, null, ctx);
    }

    public Bounds2 selfBounds(HNode t, NDocNodeRendererContext ctx) {
        return NDocValueByName.selfBounds(t, null, null, ctx);
    }

}
