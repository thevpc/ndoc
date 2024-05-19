package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.api.model.HPagePart;
import net.thevpc.halfa.api.model.HStyle;
import net.thevpc.halfa.api.model.HStyleType;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface HPartRendererContext {
    Graphics2D getGraphics();

    Rectangle2D.Double getBounds();

    default Rectangle2D.Double paintPagePart(HPagePart p) {
        return paintPagePart(p, this);
    }

    Point2D.Double getOffset();

    Rectangle2D.Double paintPagePart(HPagePart p, HPartRendererContext ctx);

    NOptional<HStyle> getStyle(HPagePart t,HStyleType s);
}
