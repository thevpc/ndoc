package net.thevpc.ndoc.engine.renderer.screen.components;

import net.thevpc.ndoc.engine.renderer.screen.DocumentView;
import net.thevpc.ndoc.api.renderer.NDocGraphics;

import java.awt.*;

public interface NDocDocumentLayer {
    void draw(DocumentView doc, Dimension size, NDocGraphics g);
}
