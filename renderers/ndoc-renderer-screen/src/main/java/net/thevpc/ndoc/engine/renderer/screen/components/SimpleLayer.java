package net.thevpc.ndoc.engine.renderer.screen.components;

import net.thevpc.ndoc.api.document.elem2d.NDocAlign;
import net.thevpc.ndoc.api.renderer.NDocGraphics;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class SimpleLayer implements NDocDocumentLayer {

    protected void drawStr(String str, NDocAlign a, Dimension size, NDocGraphics g2d) {

        Rectangle2D b = g2d.getStringBounds(str);

        int x = 0;
        int y = 0;
        switch (a) {
            case LEFT: {
                x = 10;
                y = (int) (size.getHeight() - b.getHeight());
                break;
            }
            case RIGHT: {
                x = (int) (size.getWidth() - b.getWidth()) - 10;
                y = (int) (size.getHeight() - b.getHeight());
                break;
            }
        }
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawString(str, x + 2, y + 2);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString(str, x, y);
    }
}
