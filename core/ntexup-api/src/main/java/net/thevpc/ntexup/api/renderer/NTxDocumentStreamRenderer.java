package net.thevpc.ntexup.api.renderer;

import java.io.OutputStream;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.nuts.io.NPath;

/**
 * @author vpc
 */
public interface NTxDocumentStreamRenderer extends NTxDocumentRenderer {

    NTxDocumentStreamRenderer setStreamRendererConfig(NTxDocumentStreamRendererConfig config);

    NTxDocumentStreamRendererConfig getStreamRendererConfig();

    NTxDocumentStreamRenderer setOutput(OutputStream stream);

    NTxDocumentStreamRenderer setOutput(NPath stream);

    NTxDocumentStreamRenderer renderNode(NTxNode part, OutputStream out);
}
