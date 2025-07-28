package net.thevpc.ndoc.api.extension;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.parser.ParseArgumentInfo;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.elem.NElement;

public interface NDocNodeCustomBuilderContext {
    NDocNodeCustomBuilderContext withId(String id);

    NDocNodeCustomBuilderContext withAliases(String... aliases);

    NDocNodeCustomBuilderContext withToElem(ToElemAction e);

    NDocNodeCustomBuilderContext withToElem(String... props);

    NDocNodeCustomBuilderContext withDefaultArg();

    NDocNodeCustomBuilderContext withRender(RenderAction e);

    NDocNodeCustomBuilderContext withArgNames(String... names);

    NDocNodeCustomBuilderContext withProcessArg(ProcessArgAction e);

    NDocNodeCustomBuilderContext withProcessArgs(ProcessArgAction e);

    NDocEngine engine();

    interface ToElemAction {
        NElement toElem(NDocNode item, NDocNodeCustomBuilderContext buildContext);
    }

    interface RenderAction {
        void renderMain(NDocNode p, NDocNodeRendererContext renderContext, NDocNodeCustomBuilderContext buildContext);
    }

    interface ProcessArgAction {
        boolean processArgument(ParseArgumentInfo info, NDocNodeCustomBuilderContext buildContext);
    }
}
