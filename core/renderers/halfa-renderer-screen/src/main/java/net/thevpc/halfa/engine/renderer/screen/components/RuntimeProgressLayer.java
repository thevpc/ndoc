package net.thevpc.halfa.engine.renderer.screen.components;

import net.thevpc.halfa.engine.renderer.screen.DocumentView;
import net.thevpc.halfa.spi.renderer.HGraphics;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class RuntimeProgressLayer implements HDocumentLayer {
    public void draw(DocumentView doc, Dimension size, HGraphics g) {
        if (doc.isLoading()) {
            double w2 = 200;
            double w1 = 100;
            double x0 = (int) size.getWidth() / 2 /*- w2 / 2 - 5*/;
            double y0 = (int) size.getHeight() / 2 /*- w2 / 2 - 5*/;
            long v = System.currentTimeMillis() / 10;
            System.out.println(v);
            drawProgress(null, new Color(0xff5500), (v % 360) / 360.0 * 100, x0, y0, w1, w2, g);
        }
    }

    private void drawProgress(String str, Color color, double percent, double x0, double y0, double w1, double w2, HGraphics g) {
        g.setColor(Color.GRAY);
//        g.setColor(Color.RED);
        g.setColor(color);
        g.setComposite(AlphaComposite.SrcOver.derive(0.5f));
        Area outerCircle = new Area(new Arc2D.Double(
                (x0 - w2 / 2),
                (y0 - w2 / 2),
                w2,
                w2,
                90,
                0 - (int) (360.0 * (percent / 100)),
                Arc2D.PIE
        ));
        Area innerCircle = new Area(new Ellipse2D.Double(
                (int) (x0 - w1 / 2),
                (int) (y0 - w1 / 2),
                (int) w1,
                (int) w1
        ));

        outerCircle.subtract(innerCircle);
        g.fill(outerCircle);
        if (str != null && str.length() > 0) {
            Rectangle2D b = g.getStringBounds(str);
            g.setComposite(AlphaComposite.SrcOver.derive(1f));
            g.setColor(Color.BLACK);
            g.drawString(
                    str,
                    (int) (x0 - b.getWidth() / 2),
                    (int) (y0 + b.getHeight() / 2)
            );

            g.setColor(Color.RED);
            g.setSecondaryColor(Color.BLUE);
        }
//        g.fillSphere(100, 100, 10, 10, 45, 0.6f);
    }
}
