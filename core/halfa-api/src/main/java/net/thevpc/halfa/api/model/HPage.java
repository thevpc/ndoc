package net.thevpc.halfa.api.model;

import java.util.List;

/**
 *
 * @author vpc
 */
public interface HPage extends HDocumentPart {

    HPage add(HPagePart part);

    List<HPagePart> getParts();
}
