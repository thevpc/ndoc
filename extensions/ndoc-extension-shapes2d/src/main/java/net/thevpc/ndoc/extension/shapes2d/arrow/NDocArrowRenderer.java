package net.thevpc.ndoc.extension.shapes2d.arrow;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;

import java.awt.*;
import java.awt.geom.GeneralPath;

public class NDocArrowRenderer extends NDocNodeRendererBase {
    public NDocArrowRenderer() {
        super(NDocNodeType.ARROW);
    }

    @Override
    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        NDocBounds2 b = NDocValueByName.selfBounds(p, null, null, ctx);
        double x = b.getX();
        double y = b.getY();
        double width = b.getWidth();
        double height = b.getHeight();

        Paint color = NDocValueByName.getForegroundColor(p,ctx, true);
        NDocPoint2D base = NDocObjEx.of(p.getPropertyValue("base")).asHPoint2D().orElse(null);
        if(base==null){
            Double base3 = NDocObjEx.of(p.getPropertyValue("base")).asDouble().orElse(null);
            if(base3==null){
                base=new NDocPoint2D(80, 20);
            }else{
                base=new NDocPoint2D(base3, base3);
            }
        }
        NDocPoint2D hat = NDocObjEx.of(p.getPropertyValue("hat")).asHPoint2D().orElse(null);
        if(hat==null){
            Double base3 = NDocObjEx.of(p.getPropertyValue("hat")).asDouble().orElse(null);
            if(base3==null){
                hat=new NDocPoint2D(-1, -1);
            }else{
                hat=new NDocPoint2D(base3, base3);
            }
        }
        if(hat.y<0){
            hat.y=15;
        }
        if(hat.x<0){
            hat.x=0;
        }
        double baseH=base.y*height/100.0;
        double baseW=base.x*width/100.0;
        double hatH=hat.y*height/100.0;
        double hatW=hat.x*width/100.0;

        NDocGraphics g = ctx.graphics();
        g.setPaint(color);
            GeneralPath arrow = new GeneralPath();
            arrow.moveTo(x, y+height/2);
            arrow.lineTo(x, y+height/2-baseH/2);
            arrow.lineTo(x+baseW, y+height/2-baseH/2);
            arrow.lineTo(x+baseW-hatW, y+height/2-baseH/2-hatH);
            arrow.lineTo(x+width, y+height/2);
            arrow.lineTo(x+baseW-hatW, y+height/2+baseH/2+hatH);
            arrow.lineTo(x+baseW, y+height/2+baseH/2);
            arrow.lineTo(x, y+height/2+baseH/2);
            arrow.closePath();
            g.draw(arrow);
            g.fill(arrow);
            NDocNodeRendererUtils.paintDebugBox(p, ctx, g, b);

    }

}
