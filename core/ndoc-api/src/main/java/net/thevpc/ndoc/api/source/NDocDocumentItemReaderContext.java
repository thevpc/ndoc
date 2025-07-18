package net.thevpc.ndoc.api.source;

import java.io.BufferedReader;

/**
 * @author vpc
 */
public interface NDocDocumentItemReaderContext {

    BufferedReader reader();

    String cwd();
}
