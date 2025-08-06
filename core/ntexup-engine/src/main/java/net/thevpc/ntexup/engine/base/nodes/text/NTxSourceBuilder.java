package net.thevpc.ntexup.engine.base.nodes.text;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.renderer.text.NTxTextOptions;
import net.thevpc.ntexup.api.renderer.text.NTxTextRendererBuilder;
import net.thevpc.ntexup.api.eval.NTxValue;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.text.NTextCode;
import net.thevpc.nuts.text.NTexts;

public class NTxSourceBuilder implements NTxNodeBuilder {

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.SOURCE)
                .parseParam().matchesName().matchesLeading().store(NTxPropName.LANG).then()
                .parseParam().named(NTxPropName.LANG).then()
                .parseParam().named(NTxPropName.VALUE).resolvedAsTrimmedBloc().then()
                .parseParam().named(NTxPropName.FILE).store(NTxPropName.VALUE).resolvedAsTrimmedPathTextContent().then()
                .parseDefaultParams()
                .parseParam().matchesStringOrName().store(NTxPropName.VALUE).resolvedAsTrimmedBloc().then()
                .renderText().buildText(this::renderTextBuildText)
        ;
    }

    private void renderTextBuildText(String text, NTxTextOptions options, NTxNode p, NTxNodeRendererContext renderContext, NTxTextRendererBuilder builder, NTxNodeCustomBuilderContext buildContext) {
        NTexts ttt = NTexts.of();
        NElement lng = p.getPropertyValue(NTxPropName.LANG).orNull();
        String lang = NTxValue.of(lng).asString().orNull();
        NTextCode ncode = ttt.ofCode(lang, text);
        renderContext.highlightNutsText(lang, text, ncode, p, builder);
    }

}
