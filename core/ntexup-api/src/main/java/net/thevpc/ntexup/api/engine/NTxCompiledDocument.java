package net.thevpc.ntexup.api.engine;

import net.thevpc.ntexup.api.document.NTxDocument;
import net.thevpc.ntexup.api.source.NTxSource;

import java.util.List;

public interface NTxCompiledDocument {
    NTxSource source();

    NTxDocument compiledDocument();

    boolean isCompiled();

    NTxDocument rawDocument();

    String title();

    NTxEngine engine();

    List<NTxCompiledPage> pages();

    Throwable currentThrowable();
}
