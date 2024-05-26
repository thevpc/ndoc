package net.thevpc.halfa.engine.renderer.screen.common;

import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.spi.renderer.HGraphics;

import java.awt.*;

public class Gx {
    public static void paintBackground(HGraphics g, Bounds2 a, Color color) {
        if(color!=null) {
            g.fillRect(a);
        }
    }
}
