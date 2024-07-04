package net.thevpc.halfa.spi.renderer.text;

import net.thevpc.nuts.text.NText;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;

public class HRichTextToken {

    public HRichTextTokenType type;
    public double xOffset;
    public NText tok;
    public Rectangle2D bounds;
    public String text;
    public AttributedString attributedString;
    public AttributedString attributedShadowString;
    public Font font;
    public HTextRendererBuilder.ImagePainter imagePainter;

    public HRichTextToken(HRichTextTokenType type, String text) {
        this.type = type;
        this.text = text;
    }

}
