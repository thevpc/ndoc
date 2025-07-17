package net.thevpc.ndoc.spi.renderer;

import java.io.OutputStream;

import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.nuts.io.NPath;

/**
 * @author vpc
 */
public interface NDocDocumentStreamRenderer extends NDocDocumentRenderer {

    NDocDocumentStreamRenderer setStreamRendererConfig(NDocDocumentStreamRendererConfig config);

    NDocDocumentStreamRendererConfig getStreamRendererConfig();

    NDocDocumentStreamRenderer setOutput(OutputStream stream);

    NDocDocumentStreamRenderer setOutput(NPath stream);

    NDocDocumentStreamRenderer renderNode(NDocNode part, OutputStream out);
}
