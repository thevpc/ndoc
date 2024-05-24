package net.thevpc.halfa.engine.renderer.screen.common;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.spi.HUtils;

import java.awt.*;

public class Gx {
    public static void paintBackground(Graphics2D g, Bounds2 a,Color color) {
        if(color!=null) {
            g.fillRect(
                    HUtils.intOf(a.getMinX()), HUtils.intOf(a.getMinY()),
                    HUtils.intOf(a.getWidth()), HUtils.intOf(a.getHeight())
            );
        }
    }
}
