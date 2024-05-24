package net.thevpc.halfa.engine.renderer.screen.components;

import net.thevpc.halfa.api.model.HAlign;
import net.thevpc.halfa.engine.renderer.screen.DocumentView;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class SimpleLayer implements DocumentLayer {

    protected void drawStr(String str, HAlign a, Dimension size, Graphics2D g2d) {
        Rectangle2D b = g2d.getFontMetrics().getStringBounds(str, g2d);

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
