package net.thevpc.halfa.api.source;

import java.io.BufferedReader;
import net.thevpc.halfa.api.model.HDocumentItem;

public interface HDocumentReader {

    HDocumentItem read(BufferedReader stream);
}
