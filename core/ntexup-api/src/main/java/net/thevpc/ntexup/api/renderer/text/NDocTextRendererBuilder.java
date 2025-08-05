package net.thevpc.ntexup.api.renderer.text;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.elem2d.NTxDouble2;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.text.NText;

public interface NDocTextRendererBuilder {
    void appendText(String rawText, NDocTextOptions options, NTxNode node, NDocNodeRendererContext ctx);

    void appendNText(String lang, String rawText, NText text, NTxNode node, NDocNodeRendererContext ctx);

    void appendCustom(String lang, String rawText, NDocTextOptions options, NTxNode node, NDocNodeRendererContext ctx);

    public void appendPlain(String text, NDocNodeRendererContext ctx);

    NDocRichTextRow nextLine();

    NDocRichTextRow currRow();

    NTxBounds2 computeBound(NDocNodeRendererContext ctx);

    public void setLang(String lang);

    public void setCode(String rawText);

    interface ImagePainter {
        void paint(NDocGraphics g, double x, double y);

        NTxDouble2 size();
    }

    public void render(NTxNode p, NDocNodeRendererContext ctx, NTxBounds2 bgBounds, NTxBounds2 selfBounds);

    boolean isEmpty();

    void addToken(NDocRichTextToken col);
}
