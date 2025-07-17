package net.thevpc.ndoc.api.source;

import java.io.BufferedReader;

import net.thevpc.ndoc.api.model.node.NDocNode;

public interface HDocumentReader {

    NDocNode read(BufferedReader stream);
}
