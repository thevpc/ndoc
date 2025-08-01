package net.thevpc.ndoc.elem.base.text;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.parser.NDocArgumentReader;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.text.NTexts;

public class NDocNtfBuilder implements NDocNodeCustomBuilder {

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.NTF)
                .parseParam().named(NDocPropName.VALUE).then()
                .parseParam().named(NDocPropName.FILE).store(NDocPropName.VALUE).resolvedAs(new NDocNodeCustomBuilderContext.PropResolver() {
                    @Override
                    public NDocProp resolve(String uid, NElement value, NDocArgumentReader info, NDocNodeCustomBuilderContext buildContext) {
                        NPath nPath = buildContext.engine().resolvePath(value.asString().get(), info.node());
                        info.parseContext().document().resources().add(nPath);
                        return NDocProp.ofString(uid, nPath.readString().trim());
                    }
                }).then()
                .parseDefaultParams()
                .parseParam().matchesStringOrName().store(NDocPropName.VALUE).then()
                .renderText(this::renderTextBuildText)
        ;
    }

    private void renderTextBuildText(String text, NDocTextOptions options, NDocNode p, NDocNodeRendererContext renderContext, NDocTextRendererBuilder builder, NDocNodeCustomBuilderContext buildContext) {
        renderContext.highlightNutsText("ntf", text, NTexts.of().of(text), p, builder);
    }

}
