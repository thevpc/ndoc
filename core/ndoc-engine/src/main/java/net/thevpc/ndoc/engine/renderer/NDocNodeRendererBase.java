package net.thevpc.ndoc.api.renderer;

import net.thevpc.ndoc.api.document.elem2d.primitives.*;
import net.thevpc.ndoc.api.document.elem2d.*;
import net.thevpc.ndoc.api.document.elem3d.*;
import net.thevpc.ndoc.api.document.node.*;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.eval.NDocValueByType;
import net.thevpc.ndoc.api.model.NDocSizeRequirements;
import net.thevpc.ndoc.api.eval.NDocObjEx;

public abstract class NDocNodeRendererBase implements NDocNodeRenderer {

    private String[] types;

    public NDocNodeRendererBase(String... types) {
        this.types = types;
    }

    @Override
    public NDocSizeRequirements sizeRequirements(NDocNode p, NDocNodeRendererContext ctx) {
        NDocBounds2 bounds = ctx.getBounds();
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

    public void render(NDocNode p, NDocNodeRendererContext ctx) {
        boolean v = NDocValueByName.isVisible(p, ctx);
        if (!v) {
            return;
        }
        NDocBounds2 selfBounds = selfBounds(p, ctx);
        NDocGraphics nv = null;
        try {
            if (!ctx.isDry()) {
            //if (!ctx.isDry()) {
                Rotation rotation = NDocValueByType.getRotation(p, ctx, NDocPropName.ROTATE).orNull();
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

    public abstract void renderMain(NDocNode p, NDocNodeRendererContext ctx);

    public NDocBounds2 bgBounds(NDocNode p, NDocNodeRendererContext ctx) {
        return NDocValueByName.selfBounds(p, null, null, ctx);
    }

    public NDocBounds2 selfBounds(NDocNode t, NDocNodeRendererContext ctx) {
        return NDocValueByName.selfBounds(t, null, null, ctx);
    }

    public NDocBounds2 defaultSelfBounds(NDocNode t, NDocNodeRendererContext ctx) {
        return NDocValueByName.selfBounds(t, null, null, ctx);
    }

}
