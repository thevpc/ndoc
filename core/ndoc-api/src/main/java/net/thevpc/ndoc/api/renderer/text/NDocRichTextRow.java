package net.thevpc.ndoc.api.renderer.text;

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
