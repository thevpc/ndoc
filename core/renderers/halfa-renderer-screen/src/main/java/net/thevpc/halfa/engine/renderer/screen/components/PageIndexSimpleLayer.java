package net.thevpc.halfa.engine.renderer.screen.components;

import net.thevpc.halfa.api.model.elem2d.HAlign;
import net.thevpc.halfa.engine.renderer.screen.DocumentView;
import net.thevpc.halfa.spi.renderer.HGraphics;

import java.awt.*;

public class PageIndexSimpleLayer extends SimpleLayer {
    public void draw(DocumentView doc, Dimension size, HGraphics g) {
        g.setFont(new Font("Arial", 0, 20));
        drawStr(doc.getPageUserIndex() + "/" + doc.getPagesCount(), HAlign.RIGHT, size, g);
    }
}
