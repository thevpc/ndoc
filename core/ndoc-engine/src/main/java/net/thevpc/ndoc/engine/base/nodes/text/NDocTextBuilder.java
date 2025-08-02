/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.text;

import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import  net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.renderer.*;
import net.thevpc.ndoc.api.renderer.text.*;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.reserved.util.NReservedSimpleCharQueue;

import java.util.*;

/**
 * @author vpc
 */
public class NDocTextBuilder implements NDocNodeCustomBuilder {

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

    private void consumeSpecialTokenType(NDocTextToken a, NDocNode p, NDocNodeRendererContext ctx, NDocTextRendererBuilder builder) {
        if (a == null) {
            return;
        }
        if (a instanceof NDocTextTokenFlavored) {
            NDocTextTokenFlavored b=(NDocTextTokenFlavored)a;
            builder.appendCustom(b.flavor(), b.value(), b.options(), p, ctx);
        }else{
            NDocTextTokenText b=(NDocTextTokenText)a;
            builder.appendText(b.value(), b.options(), p, ctx);
        }
    }

    public List<NDocTextToken> parseImmediate(NReservedSimpleCharQueue queue, NDocNodeRendererContext ctx,NDocNodeCustomBuilderContext builderContext) {
        NDocTextTokenParseHelper aa = new NDocTextTokenParseHelper(ctx, queue,builderContext);
        return aa.parse();
    }


}
