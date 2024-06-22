package net.thevpc.halfa.engine.renderer.screen.common;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TextUtils {
    public static void drawThrowable(Throwable str, Rectangle2D.Double bounds, Graphics2D g2d) {
        if (str == null) {
            return;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bos);
        str.printStackTrace(ps);
        ps.flush();
        drawString(bos.toString(), bounds, g2d);
    }

    public static void drawString(String str, Rectangle2D.Double bounds, Graphics2D g2d) {
        if (str == null) {
            str = "";
        }
        double x = bounds.getMinX();
        double y = bounds.getMinY();
        FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
        for (String line : str.split("\n")) {
            g2d.drawString(line, (float) x, (float) y);
            Rectangle2D b = fm.getStringBounds(line, g2d);
            y = y + b.getHeight();
        }
    }
}
