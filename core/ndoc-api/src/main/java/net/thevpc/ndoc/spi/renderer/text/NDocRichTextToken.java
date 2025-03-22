package net.thevpc.ndoc.spi.renderer.text;

import net.thevpc.nuts.text.NText;

import java.awt.geom.Rectangle2D;

public class NDocRichTextToken {

    public NDocRichTextTokenType type;
    public double xOffset;
    public NText tok;
    public Rectangle2D bounds;

    public String text;
    public NDocTextOptions textOptions=new NDocTextOptions();
    public NDocTextRendererBuilder.ImagePainter imagePainter;

    public NDocRichTextToken(NDocRichTextTokenType type, String text) {
        this.type = type;
        this.text = text;
    }


}
