package net.thevpc.ntexup.engine.renderer;

import net.thevpc.ntexup.api.document.elem2d.*;
import net.thevpc.ntexup.api.document.node.*;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.eval.NDocValueByName;
import net.thevpc.ntexup.api.eval.NDocValueByType;
import net.thevpc.ntexup.api.document.NDocSizeRequirements;
import net.thevpc.ntexup.api.eval.NDocValue;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocNodeRenderer;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;

public abstract class NDocNodeRendererBase implements NDocNodeRenderer {

    private String[] types;

    public NDocNodeRendererBase(String... types) {
        this.types = types;
    }

    @Override
    public NDocSizeRequirements sizeRequirements(NTxNode p, NDocNodeRendererContext ctx) {
        NTxBounds2 bounds = ctx.getBounds();
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

    public void render(NTxNode p, NDocNodeRendererContext ctx) {
        boolean v = NDocValueByName.isVisible(p, ctx);
        if (!v) {
            return;
        }
        NTxBounds2 selfBounds = selfBounds(p, ctx);
        NDocGraphics nv = null;
        try {
            if (!ctx.isDry()) {
            //if (!ctx.isDry()) {
                NTxRotation rotation = NDocValueByType.getRotation(p, ctx, NTxPropName.ROTATE).orNull();
                if (rotation != null) {
                    double angle = NDocValue.of(rotation.getAngle()).asDouble().orElse(0.0);
                    if (angle != 0) {
                        angle = angle / 180.0 * Math.PI;
                        if (angle != 0) {
                            NDocGraphics g = ctx.graphics();
                            nv = g.copy();
                            ///HSizeRef sr=new HSizeRef();
                            double rotX = NDocValue.of(rotation.getX()).asDouble().get() / 100.0 * selfBounds.getWidth() + selfBounds.getX();
                            double rotY = NDocValue.of(rotation.getY()).asDouble().get() / 100.0 * selfBounds.getHeight() + selfBounds.getY();
                            if (ctx.isDebug(p)) {
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

    public abstract void renderMain(NTxNode p, NDocNodeRendererContext ctx);

    public NTxBounds2 bgBounds(NTxNode p, NDocNodeRendererContext ctx) {
        return ctx.selfBounds(p, null, null);
    }

    public NTxBounds2 selfBounds(NTxNode t, NDocNodeRendererContext ctx) {
        return NDocValueByName.selfBounds(t, null, null, ctx);
    }

    public NTxBounds2 defaultSelfBounds(NTxNode t, NDocNodeRendererContext ctx) {
        return NDocValueByName.selfBounds(t, null, null, ctx);
    }

}
