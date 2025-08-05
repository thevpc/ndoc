package net.thevpc.ntexup.engine.renderer.screen.components;

import net.thevpc.ntexup.engine.renderer.screen.DocumentView;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.engine.renderer.screen.PageView;

import java.awt.*;

public interface NTxDocumentLayer {
    void draw(DocumentView doc, PageView pageView, Dimension size, NTxGraphics g);
}
