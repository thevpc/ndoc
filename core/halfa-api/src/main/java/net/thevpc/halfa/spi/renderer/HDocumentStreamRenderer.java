package net.thevpc.halfa.spi.renderer;

import java.io.OutputStream;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.nuts.io.NPath;

/**
 * @author vpc
 */
public interface HDocumentStreamRenderer extends HDocumentRenderer {

    HDocumentStreamRenderer setStreamRendererConfig(HDocumentStreamRendererConfig config);

    HDocumentStreamRendererConfig getStreamRendererConfig();

    HDocumentStreamRenderer setOutput(OutputStream stream);

    HDocumentStreamRenderer setOutput(NPath stream);

    HDocumentStreamRenderer renderNode(HNode part, OutputStream out);
}
