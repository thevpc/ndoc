package net.thevpc.halfa.api.model;

import java.util.List;

/**
 *
 * @author vpc
 */
public interface HPage extends HDocumentPart {

    HPage addPart(HPagePart part);

    List<HPagePart> getParts();
}
