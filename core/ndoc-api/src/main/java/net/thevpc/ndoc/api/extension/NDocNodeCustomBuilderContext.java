package net.thevpc.ndoc.api.ext;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.parser.ParseArgumentInfo;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.nuts.elem.NElement;

public interface NDocNodeCustomBuilderContext {
    NDocNodeCustomBuilderContext withId(String id);

    NDocNodeCustomBuilderContext withToElem(ToElemAction e);

    NDocNodeCustomBuilderContext withRender(RenderAction e);

    NDocNodeCustomBuilderContext withProcessArg(ProcessArgAction e);

    NDocNodeCustomBuilderContext withProcessArgs(ProcessArgAction e);

    NDocEngine engine();

    boolean defaultProcessArgument(ParseArgumentInfo info);
    boolean defaultProcessArguments(ParseArgumentInfo info);

    interface ToElemAction {
        NElement toElem(NDocNode item, NDocNodeCustomBuilderContext context);
    }

    interface RenderAction {
        void renderMain(NDocNode p, NDocNodeRendererContext ctx, NDocNodeCustomBuilderContext context);
    }

    interface ProcessArgAction {
        boolean processArgument(ParseArgumentInfo info, NDocNodeCustomBuilderContext context);
    }
}
