package net.thevpc.ndoc.api.source;

import java.io.BufferedReader;

/**
 * @author vpc
 */
public interface HDocumentItemReaderContext {

    BufferedReader reader();

    String cwd();
}
