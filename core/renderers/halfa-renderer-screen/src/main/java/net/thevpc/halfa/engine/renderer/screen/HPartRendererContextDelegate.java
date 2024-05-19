package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.api.model.HPagePart;
import net.thevpc.halfa.api.model.HStyle;
import net.thevpc.halfa.api.model.HStyleType;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HPartRendererContextDelegate implements HPartRendererContext {
    private HPartRendererContext base;
    private Rectangle2D.Double size;
    private Point2D.Double offset;
    private HPagePart basePart;

    public HPartRendererContextDelegate(HPagePart basePart,HPartRendererContext base, Point2D.Double offset,Rectangle2D.Double size) {
        this.basePart = basePart;
        this.base = base;
        this.size = size;
        this.offset = offset;
    }

    public Point2D.Double getOffset() {
        return offset;
    }

    public Graphics2D getGraphics() {
        return base.getGraphics();
    }

    public Rectangle2D.Double getBounds() {
        return size;
    }

    @Override
    public Rectangle2D.Double paintPagePart(HPagePart p, HPartRendererContext ctx) {
        return base.paintPagePart(p,ctx);
    }

    @Override
    public NOptional<HStyle> getStyle(HPagePart t, HStyleType s) {
        NOptional<HStyle> y = null;
        if(t!=null) {
            y = t.getStyle(s);
            if (y.isPresent()) {
                return y;
            }
        }
        if(basePart!=null){
            y = basePart.getStyle(s);
            if(y.isPresent()) {
                return y;
            }
        }
        return base.getStyle(null, s);
    }
}
