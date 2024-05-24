package net.thevpc.halfa.engine.renderer.screen.components;

import net.thevpc.halfa.api.model.HAlign;
import net.thevpc.halfa.engine.renderer.screen.DocumentView;

import java.awt.*;

public class SourceNameSimpleLayer extends SimpleLayer {
    public void draw(DocumentView doc, Dimension size, Graphics2D g) {
        g.setFont(new Font("Arial", 0, 20));
        drawStr(doc.getPageSourceName(), HAlign.LEFT, size, g);
    }
}
