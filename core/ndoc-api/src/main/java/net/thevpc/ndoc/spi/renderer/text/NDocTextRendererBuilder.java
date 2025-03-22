package net.thevpc.ndoc.spi.renderer.text;

import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.elem2d.Double2;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.text.NText;

public interface NDocTextRendererBuilder {
    void appendText(String rawText, NDocTextOptions options, HNode node, NDocNodeRendererContext ctx);

    void appendNText(String lang, String rawText, NText text, HNode node, NDocNodeRendererContext ctx);

    void appendCustom(String lang, String rawText, NDocTextOptions options, HNode node, NDocNodeRendererContext ctx);

    public void appendEq(String text, HNode node, NDocNodeRendererContext ctx);

    public void appendPlain(String text, NDocNodeRendererContext ctx);

    NDocRichTextRow nextLine();

    NDocRichTextRow currRow();

    Bounds2 computeBound(NDocNodeRendererContext ctx);

    public void setLang(String lang);

    public void setCode(String rawText);

    interface ImagePainter {
        void paint(NDocGraphics g, double x, double y);

        Double2 size();
    }

    public ImagePainter createLatex(String tex, double fontSize);

    public void render(HNode p, NDocNodeRendererContext ctx, Bounds2 bgBounds, Bounds2 selfBounds);

    boolean isEmpty();

    void addToken(NDocRichTextToken col);
}
