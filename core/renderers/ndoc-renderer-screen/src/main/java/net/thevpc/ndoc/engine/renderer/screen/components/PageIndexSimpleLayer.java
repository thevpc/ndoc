package net.thevpc.ndoc.engine.renderer.screen.components;

import net.thevpc.ndoc.api.model.elem2d.NDocAlign;
import net.thevpc.ndoc.engine.renderer.screen.DocumentView;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;

import java.awt.*;

public class PageIndexSimpleLayer extends SimpleLayer {
    public void draw(DocumentView doc, Dimension size, NDocGraphics g) {
        g.setFont(new Font("Arial", 0, 20));
        drawStr(doc.getPageUserIndex() + "/" + doc.getPagesCount(), NDocAlign.RIGHT, size, g);
    }
}
