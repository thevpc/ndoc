package net.thevpc.ntexup.engine.renderer.screen.components;

import net.thevpc.ntexup.api.document.elem2d.NTxAlign;
import net.thevpc.ntexup.engine.renderer.screen.DocumentView;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.engine.renderer.screen.PageView;

import java.awt.*;

public class PageIndexSimpleLayer extends SimpleLayer {
    public void draw(DocumentView doc, PageView pageView, Dimension size, NDocGraphics g) {
        g.setFont(new Font("Arial", 0, 20));
        drawStr(doc.getPageUserIndex() + "/" + doc.getPagesCount(), NTxAlign.RIGHT, size, g);
    }
}
