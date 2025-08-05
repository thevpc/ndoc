package net.thevpc.ntexup.engine.renderer.screen.components;

import net.thevpc.ntexup.engine.renderer.screen.DocumentView;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.engine.renderer.screen.PageView;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class PizzaProgressLayer implements NTxDocumentLayer {
    public void draw(DocumentView doc, PageView pageView, Dimension size, NTxGraphics g) {
        if (doc.isLoading()) {
            return;
        }
        double w2 = 100;
        double w1 = 50;
        g.setColor(Color.GRAY);
        double x0 = (int) size.getWidth() - w2 / 2 - 5;
        double y0 = (int) w2 / 2 + 5;
//        g.setColor(Color.RED);
        g.setColor(new Color(0x55007f));
        g.setComposite(AlphaComposite.SrcOver.derive(0.5f));
        Area outerCircle = new Area(new Arc2D.Double(
                (x0 - w2 / 2),
                (y0 - w2 / 2),
                w2,
                w2,
                90,
                0 - (int) (360.0 * (doc.getPageIndex() + 1) / doc.getPagesCount()),
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

        String str = "" + (doc.getPageUserIndex());
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
        g.fillSphere(100, 100, 10, 10, 45, 0.6f);
    }
}
