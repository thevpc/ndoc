package net.thevpc.ndoc.spi.renderer.text;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class NDocRichTextRow {

    public List<NDocRichTextToken> tokens = new ArrayList<>();
    public double yOffset;
    public Rectangle2D textBounds;

    public void addToken(NDocRichTextToken r) {
        if (r != null) {
            tokens.add(r);
        }
    }
}
