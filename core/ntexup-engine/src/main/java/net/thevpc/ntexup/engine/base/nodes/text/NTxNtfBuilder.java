package net.thevpc.ntexup.engine.base.nodes.text;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.renderer.text.NTxTextOptions;
import net.thevpc.ntexup.api.renderer.text.NTxTextRendererBuilder;
import net.thevpc.nuts.text.NTexts;

public class NTxNtfBuilder implements NTxNodeBuilder {

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.NTF)
                .parseParam().named(NTxPropName.VALUE).resolvedAsTrimmedBloc().then()
                .parseParam().named(NTxPropName.FILE).store(NTxPropName.VALUE).resolvedAsTrimmedPathTextContent().then()
                .parseDefaultParams()
                .parseParam().matchesStringOrName().store(NTxPropName.VALUE).resolvedAsTrimmedBloc().then()
                .renderText(this::renderTextBuildText)
        ;
    }

    private void renderTextBuildText(String text, NTxTextOptions options, NTxNode p, NTxNodeRendererContext renderContext, NTxTextRendererBuilder builder, NTxNodeCustomBuilderContext buildContext) {
        renderContext.highlightNutsText("ntf", text, NTexts.of().of(text), p, builder);
    }

}
