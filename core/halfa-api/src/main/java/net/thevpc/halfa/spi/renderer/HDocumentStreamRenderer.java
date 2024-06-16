package net.thevpc.halfa.spi.renderer;

import java.io.OutputStream;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.nuts.io.NPath;

/**
 * @author vpc
 */
public interface HDocumentStreamRenderer extends HDocumentRenderer {

    void setOutput(OutputStream stream);

    void setOutput(NPath stream);

    void renderNode(HNode part, OutputStream out);
}
