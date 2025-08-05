package net.thevpc.ntexup.engine.base.nodes.text;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.style.NTxPropName;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.ntexup.api.renderer.text.NDocTextOptions;
import net.thevpc.ntexup.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ntexup.api.eval.NDocValue;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.text.NTextCode;
import net.thevpc.nuts.text.NTexts;

public class NDocSourceBuilder implements NDocNodeBuilder {

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.SOURCE)
                .parseParam().matchesName().matchesLeading().store(NTxPropName.LANG).then()
                .parseParam().named(NTxPropName.LANG).then()
                .parseParam().named(NTxPropName.VALUE).resolvedAsTrimmedBloc().then()
                .parseParam().named(NTxPropName.FILE).store(NTxPropName.VALUE).resolvedAsTrimmedPathTextContent().then()
                .parseDefaultParams()
                .parseParam().matchesStringOrName().store(NTxPropName.VALUE).resolvedAsTrimmedBloc().then()
                .renderText(this::renderTextBuildText)
        ;
    }

    private void renderTextBuildText(String text, NDocTextOptions options, NTxNode p, NDocNodeRendererContext renderContext, NDocTextRendererBuilder builder, NTxNodeCustomBuilderContext buildContext) {
        NTexts ttt = NTexts.of();
        NElement lng = p.getPropertyValue(NTxPropName.LANG).orNull();
        String lang = NDocValue.of(lng).asString().orNull();
        NTextCode ncode = ttt.ofCode(lang, text);
        renderContext.highlightNutsText(lang, text, ncode, p, builder);
    }

}
