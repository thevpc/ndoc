package net.thevpc.ndoc.engine.renderer.screen.components;

import net.thevpc.ndoc.engine.renderer.screen.DocumentView;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.engine.renderer.screen.PageView;

import java.awt.*;

public interface NDocDocumentLayer {
    void draw(DocumentView doc, PageView pageView, Dimension size, NDocGraphics g);
}
