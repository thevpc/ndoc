package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.nuts.io.NPath;

public interface HDocumentRenderer {

    void render(HDocument document);

    void renderPath(NPath path);

    void renderSupplier(HDocumentRendererSupplier document);

    void addRendererListener(HDocumentRendererListener listener);

    HMessageList getMessages();

    HDocumentRenderer setMessages(HMessageList messages);
}
