package net.thevpc.halfa.spi.renderer.text;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.Double2;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.nuts.text.NText;

public interface HTextRendererBuilder {
    void appendNText(String lang, String rawText, NText text, HNode node, HNodeRendererContext ctx);

    void appendCustom(String lang, String rawText, HNode node, HNodeRendererContext ctx);

    public void appendEq(String text, HNode node, HNodeRendererContext ctx);

    public void appendPlain(String text, HNodeRendererContext ctx);

    HRichTextRow nextLine();

    HRichTextRow currRow();

    Bounds2 computeBound(HNodeRendererContext ctx);

    public void setLang(String lang);

    public void setCode(String rawText);

    interface ImagePainter {
        void paint(HGraphics g, double x, double y);

        Double2 size();
    }

    public ImagePainter createLatex(String tex, double fontSize);

    public void render(HNode p, HNodeRendererContext ctx, Bounds2 bgBounds, Bounds2 selfBounds);

    boolean isEmpty();

    void addToken(HRichTextToken col);
}
