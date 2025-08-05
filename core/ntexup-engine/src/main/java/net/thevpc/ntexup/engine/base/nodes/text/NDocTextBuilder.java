/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.base.nodes.text;

import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.renderer.*;
import net.thevpc.ntexup.api.renderer.text.*;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.reserved.util.NReservedSimpleCharQueue;

import java.util.*;

/**
 * @author vpc
 */
public class NDocTextBuilder implements NDocNodeBuilder {

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.TEXT)
                .parseAny(x->true)
                .parseParam().named(NTxPropName.VALUE).resolvedAsTrimmedBloc().then()
                .parseParam().named(NTxPropName.FILE).store(NTxPropName.VALUE).resolvedAsTrimmedPathTextContent().then()
                .parseDefaultParams()
                .parseParam().matchesStringOrName().store(NTxPropName.VALUE).resolvedAsTrimmedBloc().then()
                .renderText(this::buildText,this::parseImmediate)
        ;
    }

    public void buildText(String text, NDocTextOptions options, NTxNode p, NDocNodeRendererContext ctx, NDocTextRendererBuilder builder, NTxNodeCustomBuilderContext builderContext) {
        NDocTextTokenParseHelper aa = new NDocTextTokenParseHelper(ctx, new NReservedSimpleCharQueue(ctx.engine().tools().trimBloc(text).toCharArray()),builderContext);
        List<NDocTextToken> all=aa.parse();
        for (NDocTextToken a : all) {
            consumeSpecialTokenType(a, p, ctx, builder);
        }
    }

    private void consumeSpecialTokenType(NDocTextToken a, NTxNode node, NDocNodeRendererContext ctx, NDocTextRendererBuilder builder) {
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

    public List<NDocTextToken> parseImmediate(NReservedSimpleCharQueue queue, NDocNodeRendererContext ctx, NTxNodeCustomBuilderContext builderContext) {
        NDocTextTokenParseHelper aa = new NDocTextTokenParseHelper(ctx, queue,builderContext);
        return aa.parse();
    }


}
