/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.engine.base.nodes.text;

import net.thevpc.ndoc.api.extension.NDocNodeBuilder;
import net.thevpc.ndoc.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import  net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.renderer.text.NDocRichTextToken;
import net.thevpc.ndoc.api.renderer.text.NDocRichTextTokenType;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererBuilder;
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

    private void renderTextBuildText(String text, NDocTextOptions options, NDocNode p, NDocNodeRendererContext renderContext, NDocTextRendererBuilder builder, NDocNodeCustomBuilderContext buildContext) {
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
