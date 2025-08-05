package net.thevpc.ntexup.api.renderer;

import java.io.OutputStream;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.nuts.io.NPath;

/**
 * @author vpc
 */
public interface NDocDocumentStreamRenderer extends NDocDocumentRenderer {

    NDocDocumentStreamRenderer setStreamRendererConfig(NDocDocumentStreamRendererConfig config);

    NDocDocumentStreamRendererConfig getStreamRendererConfig();

    NDocDocumentStreamRenderer setOutput(OutputStream stream);

    NDocDocumentStreamRenderer setOutput(NPath stream);

    NDocDocumentStreamRenderer renderNode(NTxNode part, OutputStream out);
}
