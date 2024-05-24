package net.thevpc.halfa.engine.renderer.screen.components;

import net.thevpc.halfa.api.model.HAlign;
import net.thevpc.halfa.engine.renderer.screen.DocumentView;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class PageIndexSimpleLayer extends SimpleLayer {
    public void draw(DocumentView doc, Dimension size, Graphics2D g) {
        g.setFont(new Font("Arial", 0, 20));
        drawStr(doc.getPageUserIndex() + "/" + doc.getPagesCount(), HAlign.RIGHT, size, g);
    }
}
