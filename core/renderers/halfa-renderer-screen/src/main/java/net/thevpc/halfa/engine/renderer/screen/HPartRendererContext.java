package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.api.style.HStyleType;
import net.thevpc.halfa.engine.renderer.screen.renderers.containers.HSizeRequirements;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public interface HPartRendererContext {
    Rectangle2D.Double getGlobalBounds();

    Graphics2D getGraphics();

    Rectangle2D.Double getBounds();

    default Rectangle2D.Double paintPagePart(HNode p) {
        return paintPagePart(p, this);
    }

    default HSizeRequirements computeSizeRequirements(HNode p) {
        return computeSizeRequirements(p, this);
    }

    Rectangle2D.Double paintPagePart(HNode p, HPartRendererContext ctx);

    HEngine engine();

    HDocumentFactory documentFactory();

    HSizeRequirements computeSizeRequirements(HNode p, HPartRendererContext ctx);

    NOptional<HStyle> getStyle(HNode t, HStyleType s);
}
