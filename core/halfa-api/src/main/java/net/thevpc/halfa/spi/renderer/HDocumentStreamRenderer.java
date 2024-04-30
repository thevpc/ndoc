package net.thevpc.halfa.spi.renderer;

import java.io.OutputStream;
import net.thevpc.halfa.api.model.HDocument;
import net.thevpc.halfa.api.model.HDocumentPart;
import net.thevpc.nuts.io.NPath;

/**
 *
 * @author vpc
 */
public interface HDocumentStreamRenderer {

    void render(HDocument document, OutputStream stream);

    void render(HDocument document, NPath path);

    void render(HDocumentPart part, OutputStream out);
}
