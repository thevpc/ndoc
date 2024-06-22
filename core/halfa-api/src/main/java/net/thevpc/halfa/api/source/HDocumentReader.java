package net.thevpc.halfa.api.source;

import java.io.BufferedReader;

import net.thevpc.halfa.api.node.HNode;

public interface HDocumentReader {

    HNode read(BufferedReader stream);
}
