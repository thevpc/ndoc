package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.api.style.HStyleType;
import net.thevpc.halfa.engine.renderer.screen.renderers.containers.HSizeRequirements;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;

public interface HPartRendererContext {
    Bounds2 getGlobalBounds();

    Graphics2D getGraphics();

    Bounds2 getBounds();

    default Bounds2 paintPagePart(HNode p) {
        return paintPagePart(p, this);
    }

    default HSizeRequirements computeSizeRequirements(HNode p) {
        return computeSizeRequirements(p, this);
    }

    Bounds2 paintPagePart(HNode p, HPartRendererContext ctx);

    HEngine engine();

    HDocumentFactory documentFactory();

    HSizeRequirements computeSizeRequirements(HNode p, HPartRendererContext ctx);

    NOptional<HStyle> getStyle(HNode t, HStyleType s);
}
