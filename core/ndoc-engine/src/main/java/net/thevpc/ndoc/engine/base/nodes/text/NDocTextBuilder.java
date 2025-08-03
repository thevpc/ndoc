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
import net.thevpc.ndoc.api.renderer.*;
import net.thevpc.ndoc.api.renderer.text.*;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.reserved.util.NReservedSimpleCharQueue;

import java.util.*;

/**
 * @author vpc
 */
public class NDocTextBuilder implements NDocNodeBuilder {

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.TEXT)
                .parseAny(x->true)
                .parseParam().named(NDocPropName.VALUE).resolvedAsTrimmedBloc().then()
                .parseParam().named(NDocPropName.FILE).store(NDocPropName.VALUE).resolvedAsTrimmedPathTextContent().then()
                .parseDefaultParams()
                .parseParam().matchesStringOrName().store(NDocPropName.VALUE).resolvedAsTrimmedBloc().then()
                .renderText(this::buildText,this::parseImmediate)
        ;
    }

    public void buildText(String text, NDocTextOptions options, NDocNode p, NDocNodeRendererContext ctx, NDocTextRendererBuilder builder,NDocNodeCustomBuilderContext builderContext) {
        NDocTextTokenParseHelper aa = new NDocTextTokenParseHelper(ctx, new NReservedSimpleCharQueue(ctx.engine().tools().trimBloc(text).toCharArray()),builderContext);
        List<NDocTextToken> all=aa.parse();
        for (NDocTextToken a : all) {
            consumeSpecialTokenType(a, p, ctx, builder);
        }
    }

    private void consumeSpecialTokenType(NDocTextToken a, NDocNode node, NDocNodeRendererContext ctx, NDocTextRendererBuilder builder) {
        if (a == null) {
            return;
        }
        if (a instanceof NDocTextTokenFlavored) {
            NDocTextTokenFlavored b=(NDocTextTokenFlavored)a;
            // interpolation is the to be done by the flavor implementation
            builder.appendCustom(b.flavor(), b.value(), b.options(), node, ctx);
        }else{
            NDocTextTokenText b=(NDocTextTokenText)a;
            // apply interpolation
            String txt2 = ctx.engine().evalExpression(NElement.ofString(b.value()), node, ctx.varProvider()).asStringValue().get();
            builder.appendText(txt2, b.options(), node, ctx);
        }
    }

    public List<NDocTextToken> parseImmediate(NReservedSimpleCharQueue queue, NDocNodeRendererContext ctx,NDocNodeCustomBuilderContext builderContext) {
        NDocTextTokenParseHelper aa = new NDocTextTokenParseHelper(ctx, queue,builderContext);
        return aa.parse();
    }


}
