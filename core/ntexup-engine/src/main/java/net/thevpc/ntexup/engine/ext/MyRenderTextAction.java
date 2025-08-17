package net.thevpc.ntexup.engine.ext;

import net.thevpc.ntexup.api.engine.NTxNodeBuilderContext;

class MyRenderTextAction implements NTxNodeBuilderContext.RenderTextAction {
    public NTxNodeBuilderContextImpl base;
    public NTxNodeBuilderContext.RenderTextBuildAction buildAction;
    public NTxNodeBuilderContext.RenderTextParseTokensAction parseTokensAction;
    public String[] renderEmbeddedTextStartSeparators;

    public MyRenderTextAction(NTxNodeBuilderContextImpl base) {
        this.base = base;
    }

    @Override
    public NTxNodeBuilderContext.RenderTextAction buildText(NTxNodeBuilderContext.RenderTextBuildAction a) {
        this.buildAction = a;
        return this;
    }

    @Override
    public NTxNodeBuilderContext.RenderTextAction parseTokens(NTxNodeBuilderContext.RenderTextParseTokensAction embeddedTextAction) {
        this.parseTokensAction = embeddedTextAction;
        return this;
    }

    @Override
    public NTxNodeBuilderContext.RenderTextAction startSeparators(String... startSeparators) {
        this.renderEmbeddedTextStartSeparators = startSeparators;
        return this;
    }

    @Override
    public NTxNodeBuilderContext then() {
        return end();
    }

    @Override
    public NTxNodeBuilderContext end() {
        return base;
    }
}
