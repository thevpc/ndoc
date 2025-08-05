/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.base.nodes.text;

import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.renderer.text.NTxRichTextToken;
import net.thevpc.ntexup.api.renderer.text.NTxRichTextTokenType;
import net.thevpc.ntexup.api.renderer.text.NTxTextOptions;
import net.thevpc.ntexup.api.renderer.text.NTxTextRendererBuilder;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NStringUtils;

/**
 * @author vpc
 */
public class NTxPlainTextBuilder implements NTxNodeBuilder {

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.PLAIN)
                .parseParam().named(NTxPropName.VALUE).resolvedAsTrimmedBloc().then()
                .parseParam().named(NTxPropName.FILE).store(NTxPropName.VALUE).resolvedAsTrimmedPathTextContent().then()
                .parseDefaultParams()
                .parseParam().matchesStringOrName().store(NTxPropName.VALUE).resolvedAsTrimmedBloc().then()
                .renderText(this::renderTextBuildText)
        ;
    }

    private void renderTextBuildText(String text, NTxTextOptions options, NTxNode p, NTxNodeRendererContext renderContext, NTxTextRendererBuilder builder, NTxNodeCustomBuilderContext buildContext) {
//        Paint fg = rendererContext.getForegroundColor(p,true);
        NElement d = p.getPropertyValue(NTxPropName.VALUE).orElse(NElement.ofString(""));

        String message = NStringUtils.trim(d.asStringValue().get());
        String[] allLines = message.trim().split("[\n]");
        for (int i = 0; i < allLines.length; i++) {
            allLines[i] = allLines[i].trim();
            NTxRichTextToken c = new NTxRichTextToken(NTxRichTextTokenType.PLAIN, allLines[i]);
            NTxGraphics g = renderContext.graphics();
            g.setFont(c.textOptions.font);
            c.bounds = g.getStringBounds(c.text);
            builder.nextLine().addToken(c);
        }
    }

}
