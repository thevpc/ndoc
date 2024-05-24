package net.thevpc.halfa.engine.renderer.screen.components;

import net.thevpc.halfa.engine.renderer.screen.DocumentView;

import java.awt.*;

public interface DocumentLayer {
    void draw(DocumentView doc, Dimension size,Graphics2D g) ;
}
