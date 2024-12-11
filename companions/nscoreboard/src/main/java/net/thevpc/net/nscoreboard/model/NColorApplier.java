package net.thevpc.net.nscoreboard.model;

import net.thevpc.net.nscoreboard.engine.PaintContext;

import java.awt.*;

public interface NColorApplier {
    static NColorApplier of(Color color) {
        return c -> c.graphics().setColor(color);
    }

    static NColorApplier ofGradient(Color color, Color color2) {
        return c -> {
            Rectangle b = c.bounds();
            c.graphics().setPaint(new GradientPaint(b.x, b.y, color, b.x + b.width, b.y + b.height, color2));
        };
    }

    void apply(PaintContext g2d);
}
