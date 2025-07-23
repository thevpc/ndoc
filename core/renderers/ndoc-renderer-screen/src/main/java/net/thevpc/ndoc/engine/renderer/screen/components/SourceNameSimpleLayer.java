package net.thevpc.ndoc.engine.renderer.screen.components;

import net.thevpc.ndoc.api.document.elem2d.NDocAlign;
import net.thevpc.ndoc.engine.renderer.screen.DocumentView;
import net.thevpc.ndoc.api.renderer.NDocGraphics;

import java.awt.*;

public class SourceNameSimpleLayer extends SimpleLayer {
    public void draw(DocumentView doc, Dimension size, NDocGraphics g) {
        g.setFont(new Font("Arial", 0, 20));
        drawStr(doc.getPageSourceName(), NDocAlign.LEFT, size, g);
    }
}
