package net.thevpc.halfa.spi.renderer.text;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class HRichTextRow {

    public List<HRichTextToken> tokens = new ArrayList<>();
    public double yOffset;
    public Rectangle2D textBounds;

    public void addToken(HRichTextToken r) {
        if (r != null) {
            tokens.add(r);
        }
    }
}
