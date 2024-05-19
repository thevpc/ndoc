package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.api.model.HPagePart;
import net.thevpc.halfa.api.model.HStyle;
import net.thevpc.halfa.api.model.HStyleType;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class HPartRendererContextImpl implements HPartRendererContext {
    private Graphics2D g;
    private Rectangle2D.Double size;
    private Point.Double offset;

    public HPartRendererContextImpl(Graphics2D g, Point.Double offset,Dimension size) {
        this.g = g;
        this.offset = offset;
        this.size = new Rectangle2D.Double(0,0,size.width,size.height);
    }

    @Override
    public Point.Double getOffset() {
        return offset;
    }

    public Graphics2D getGraphics() {
        return g;
    }

    public Rectangle2D.Double getBounds() {
        return size;
    }

    @Override
    public NOptional<HStyle> getStyle(HPagePart t, HStyleType s) {
        if(t!=null) {
            return t.getStyle(s);
        }
        return NOptional.ofNamedEmpty("style "+s);
    }
}
