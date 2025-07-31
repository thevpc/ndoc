/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.text;

import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.parser.NDocArgumentParseInfo;
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
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NStringUtils;

/**
 * @author vpc
 */
public class NDocPlainTextBuilder implements NDocNodeCustomBuilder {

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.PLAIN)
                .parseParam().named(NDocPropName.VALUE).then()
                .parseParam().named(NDocPropName.FILE).set(NDocPropName.VALUE).resolvedAs(new NDocNodeCustomBuilderContext.PropResolver() {
                    @Override
                    public NDocProp resolve(String uid, NElement value, NDocArgumentParseInfo info, NDocNodeCustomBuilderContext buildContext) {
                        NPath nPath = buildContext.engine().resolvePath(value.asString().get(), info.node());
                        info.getContext().document().resources().add(nPath);
                        return NDocProp.ofString(uid, nPath.readString().trim());
                    }
                }).then()
                .parseParam().matchesStringOrName().set(NDocPropName.VALUE).then()
                .renderText(this::renderTextBuildText)
        ;
    }

    private void renderTextBuildText(String text, NDocTextOptions options, NDocNode p, NDocNodeRendererContext renderContext, NDocTextRendererBuilder builder, NDocNodeCustomBuilderContext buildContext) {
//        Paint fg = NDocValueByName.getForegroundColor(p, renderContext,true);
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
