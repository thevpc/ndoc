package net.thevpc.halfa.engine.renderer.screen.components;

import net.thevpc.halfa.engine.renderer.screen.DocumentView;
import net.thevpc.halfa.spi.renderer.HGraphics;

import java.awt.*;

public interface HDocumentLayer {
    void draw(DocumentView doc, Dimension size, HGraphics g);
}
