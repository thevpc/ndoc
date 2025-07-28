package net.thevpc.ndoc.engine;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.parser.ParseArgumentInfo;

class MyNDocNodeCustomBuilderContext implements NDocNodeCustomBuilderContext {
    @Override
    public NDocNodeCustomBuilderContext withId(String id) {
        return null;
    }

    @Override
    public NDocNodeCustomBuilderContext withToElem(ToElemAction e) {
        return null;
    }

    @Override
    public NDocNodeCustomBuilderContext withRender(RenderAction e) {
        return null;
    }

    @Override
    public NDocNodeCustomBuilderContext withProcessArg(ProcessArgAction e) {
        return null;
    }

    @Override
    public NDocNodeCustomBuilderContext withProcessArgs(ProcessArgAction e) {
        return null;
    }

    @Override
    public NDocEngine engine() {
        return null;
    }

    @Override
    public boolean defaultProcessArgument(ParseArgumentInfo info) {
        return false;
    }

    @Override
    public boolean defaultProcessArguments(ParseArgumentInfo info) {
        return false;
    }
}
