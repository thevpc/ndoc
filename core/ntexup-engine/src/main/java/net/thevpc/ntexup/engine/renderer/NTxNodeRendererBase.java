package net.thevpc.ntexup.engine.renderer;

import net.thevpc.ntexup.api.document.elem2d.*;
import net.thevpc.ntexup.api.document.node.*;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.eval.NTxValueByName;
import net.thevpc.ntexup.api.eval.NTxValueByType;
import net.thevpc.ntexup.api.document.NTxSizeRequirements;
import net.thevpc.ntexup.api.eval.NTxValue;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.api.renderer.NTxNodeRenderer;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;

public abstract class NTxNodeRendererBase implements NTxNodeRenderer {

    private String[] types;

    public NTxNodeRendererBase(String... types) {
        this.types = types;
    }

    @Override
    public NTxSizeRequirements sizeRequirements(NTxNode p, NTxNodeRendererContext ctx) {
        NTxBounds2 bounds = ctx.getBounds();
        return new NTxSizeRequirements(
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

    public void render(NTxNode p, NTxNodeRendererContext ctx) {
        boolean v = NTxValueByName.isVisible(p, ctx);
        if (!v) {
            return;
        }
        NTxBounds2 selfBounds = selfBounds(p, ctx);
        NTxGraphics nv = null;
        try {
            if (!ctx.isDry()) {
            //if (!ctx.isDry()) {
                NTxRotation rotation = NTxValueByType.getRotation(p, ctx, NTxPropName.ROTATE).orNull();
                if (rotation != null) {
                    double angle = NTxValue.of(rotation.getAngle()).asDouble().orElse(0.0);
                    if (angle != 0) {
                        angle = angle / 180.0 * Math.PI;
                        if (angle != 0) {
                            NTxGraphics g = ctx.graphics();
                            nv = g.copy();
                            ///HSizeRef sr=new HSizeRef();
                            double rotX = NTxValue.of(rotation.getX()).asDouble().get() / 100.0 * selfBounds.getWidth() + selfBounds.getX();
                            double rotY = NTxValue.of(rotation.getY()).asDouble().get() / 100.0 * selfBounds.getHeight() + selfBounds.getY();
                            if (ctx.isDebug(p)) {
                                g.setColor(NTxValueByName.getDebugColor(p, ctx));
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

    public abstract void renderMain(NTxNode p, NTxNodeRendererContext ctx);

    public NTxBounds2 bgBounds(NTxNode p, NTxNodeRendererContext ctx) {
        return ctx.selfBounds(p, null, null);
    }

    public NTxBounds2 selfBounds(NTxNode t, NTxNodeRendererContext ctx) {
        return NTxValueByName.selfBounds(t, null, null, ctx);
    }

    public NTxBounds2 defaultSelfBounds(NTxNode t, NTxNodeRendererContext ctx) {
        return NTxValueByName.selfBounds(t, null, null, ctx);
    }

}
