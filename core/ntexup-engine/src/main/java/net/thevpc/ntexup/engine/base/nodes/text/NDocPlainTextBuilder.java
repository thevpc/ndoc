/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.base.nodes.text;

import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NDocNodeType;
import  net.thevpc.ntexup.api.document.style.NDocPropName;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.ntexup.api.renderer.text.NDocRichTextToken;
import net.thevpc.ntexup.api.renderer.text.NDocRichTextTokenType;
import net.thevpc.ntexup.api.renderer.text.NDocTextOptions;
import net.thevpc.ntexup.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NStringUtils;

/**
 * @author vpc
 */
public class NDocPlainTextBuilder implements NDocNodeBuilder {

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.PLAIN)
                .parseParam().named(NDocPropName.VALUE).resolvedAsTrimmedBloc().then()
                .parseParam().named(NDocPropName.FILE).store(NDocPropName.VALUE).resolvedAsTrimmedPathTextContent().then()
                .parseDefaultParams()
                .parseParam().matchesStringOrName().store(NDocPropName.VALUE).resolvedAsTrimmedBloc().then()
                .renderText(this::renderTextBuildText)
        ;
    }

    private void renderTextBuildText(String text, NDocTextOptions options, NTxNode p, NDocNodeRendererContext renderContext, NDocTextRendererBuilder builder, NDocNodeCustomBuilderContext buildContext) {
//        Paint fg = rendererContext.getForegroundColor(p,true);
        NElement d = p.getPropertyValue(NDocPropName.VALUE).orElse(NElement.ofString(""));

        String message = NStringUtils.trim(d.asStringValue().get());
        String[] allLines = message.trim().split("[\n]");
        for (int i = 0; i < allLines.length; i++) {
            allLines[i] = allLines[i].trim();
            NDocRichTextToken c = new NDocRichTextToken(NDocRichTextTokenType.PLAIN, allLines[i]);
            NDocGraphics g = renderContext.graphics();
            g.setFont(c.textOptions.font);
            c.bounds = g.getStringBounds(c.text);
            builder.nextLine().addToken(c);
        }
    }

}
