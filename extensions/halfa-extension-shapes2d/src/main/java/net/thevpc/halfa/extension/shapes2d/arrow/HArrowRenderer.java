package net.thevpc.halfa.extension.shapes2d.arrow;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.spi.renderer.HNodeRendererBase;
import net.thevpc.halfa.spi.util.HNodeRendererUtils;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

public class HArrowRenderer extends HNodeRendererBase {
    public HArrowRenderer() {
        super(HNodeType.ARROW);
    }

    @Override
    public void renderMain(HNode p, HNodeRendererContext ctx) {
        Bounds2 b = HValueByName.selfBounds(p, null, null, ctx);
        double x = b.getX();
        double y = b.getY();
        double width = b.getWidth();
        double height = b.getHeight();

        Paint color = HValueByName.getForegroundColor(p,ctx, true);
        HPoint2D base = ObjEx.of(p.getPropertyValue("base")).asHPoint2D().orElse(null);
        if(base==null){
            Double base3 = ObjEx.of(p.getPropertyValue("base")).asDouble().orElse(null);
            if(base3==null){
                base=new HPoint2D(80, 20);
            }else{
                base=new HPoint2D(base3, base3);
            }
        }
        HPoint2D hat = ObjEx.of(p.getPropertyValue("hat")).asHPoint2D().orElse(null);
        if(hat==null){
            Double base3 = ObjEx.of(p.getPropertyValue("hat")).asDouble().orElse(null);
            if(base3==null){
                hat=new HPoint2D(-1, -1);
            }else{
                hat=new HPoint2D(base3, base3);
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

        HGraphics g = ctx.graphics();
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
            HNodeRendererUtils.paintDebugBox(p, ctx, g, b);

    }

}
