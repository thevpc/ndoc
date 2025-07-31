package net.thevpc.ndoc.elem.base.text.source;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.parser.NDocArgumentParseInfo;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.renderer.NDocTextToken;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.reserved.util.NReservedSimpleCharQueue;
import net.thevpc.nuts.text.NTextCode;
import net.thevpc.nuts.text.NTexts;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

import java.util.List;

public class NDocSourceBuilder implements NDocNodeCustomBuilder {

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.SOURCE)
                .parseParam().named(NDocPropName.VALUE, NDocPropName.LANG).then()
                .parseParam().named(NDocPropName.FILE).set(NDocPropName.VALUE).resolvedAs(new NDocNodeCustomBuilderContext.PropResolver() {
                    @Override
                    public NDocProp resolve(String uid, NElement value, NDocArgumentParseInfo info, NDocNodeCustomBuilderContext buildContext) {
                        NPath nPath = buildContext.engine().resolvePath(value.asString().get(), info.getNode());
                        info.getContext().document().resources().add(nPath);
                        return NDocProp.ofString(uid, nPath.readString().trim());
                    }
                }).then()
                .parseParam().matchesName().set(NDocPropName.LANG).then()
                .parseParam().matchesStringOrName().set(NDocPropName.VALUE).then()
                .renderText(this::renderTextBuildText)
        ;
    }

    private void renderTextBuildText(String text, NDocTextOptions options, NDocNode p, NDocNodeRendererContext renderContext, NDocTextRendererBuilder builder, NDocNodeCustomBuilderContext buildContext) {
        NTexts ttt = NTexts.of();
        NElement lng = p.getPropertyValue(NDocPropName.LANG).orNull();
        String lang = NDocObjEx.of(lng).asString().orNull();
        NTextCode ncode = ttt.ofCode(lang, text);
        renderContext.highlightNutsText(lang, text, ncode, p, builder);
    }

}
