package net.thevpc.halfa.api.source;

import java.io.BufferedReader;

import net.thevpc.halfa.api.model.node.HNode;

public interface HDocumentReader {

    HNode read(BufferedReader stream);
}
