package net.thevpc.ntexup.engine.ext;

import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;

class MyRenderTextAction implements NTxNodeCustomBuilderContext.RenderTextAction {
    public NTxNodeCustomBuilderContextImpl base;
    public NTxNodeCustomBuilderContext.RenderTextBuildAction buildAction;
    public NTxNodeCustomBuilderContext.RenderTextParseTokensAction parseTokensAction;
    public String[] renderEmbeddedTextStartSeparators;

    public MyRenderTextAction(NTxNodeCustomBuilderContextImpl base) {
        this.base = base;
    }

    @Override
    public NTxNodeCustomBuilderContext.RenderTextAction buildText(NTxNodeCustomBuilderContext.RenderTextBuildAction a) {
        this.buildAction = a;
        return this;
    }

    @Override
    public NTxNodeCustomBuilderContext.RenderTextAction parseTokens(NTxNodeCustomBuilderContext.RenderTextParseTokensAction embeddedTextAction) {
        this.parseTokensAction = embeddedTextAction;
        return this;
    }

    @Override
    public NTxNodeCustomBuilderContext.RenderTextAction startSeparators(String... startSeparators) {
        this.renderEmbeddedTextStartSeparators = startSeparators;
        return this;
    }

    @Override
    public NTxNodeCustomBuilderContext then() {
        return end();
    }

    @Override
    public NTxNodeCustomBuilderContext end() {
        return base;
    }
}
