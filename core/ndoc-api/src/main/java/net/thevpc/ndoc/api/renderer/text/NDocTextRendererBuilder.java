package net.thevpc.ndoc.api.renderer.text;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.text.NText;

public interface NDocTextRendererBuilder {
    void appendText(String rawText, NDocTextOptions options, NDocNode node, NDocNodeRendererContext ctx);

    void appendNText(String lang, String rawText, NText text, NDocNode node, NDocNodeRendererContext ctx);

    void appendCustom(String lang, String rawText, NDocTextOptions options, NDocNode node, NDocNodeRendererContext ctx);

    public void appendPlain(String text, NDocNodeRendererContext ctx);

    NDocRichTextRow nextLine();

    NDocRichTextRow currRow();

    NDocBounds2 computeBound(NDocNodeRendererContext ctx);

    public void setLang(String lang);

    public void setCode(String rawText);

    interface ImagePainter {
        void paint(NDocGraphics g, double x, double y);

        NDocDouble2 size();
    }

    public void render(NDocNode p, NDocNodeRendererContext ctx, NDocBounds2 bgBounds, NDocBounds2 selfBounds);

    boolean isEmpty();

    void addToken(NDocRichTextToken col);
}
