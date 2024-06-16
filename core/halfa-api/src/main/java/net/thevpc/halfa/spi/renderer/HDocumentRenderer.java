package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;

import java.util.function.Supplier;

public interface HDocumentRenderer {
    void render(HDocument document);

    void renderSupplier(Supplier<HDocument> document);

    void addRendererListener(HDocumentRendererListener listener);

    HMessageList getMessages();

    HDocumentRenderer setMessages(HMessageList messages);
}
