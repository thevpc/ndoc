package net.thevpc.ntexup.engine.util;

import net.thevpc.ntexup.api.document.node.NDocNodeType;
import net.thevpc.ntexup.api.document.NDocument;
import net.thevpc.ntexup.api.document.node.NTxNode;

import java.util.ArrayList;
import java.util.List;

public class NDocUtilsPages {
    public static List<NTxNode> resolvePages(NDocument document) {
        List<NTxNode> all = new ArrayList<>();
        fillPages(document.root(), all);
        return all;
    }

    public static List<NTxNode> resolvePages(NTxNode part) {
        List<NTxNode> all = new ArrayList<>();
        fillPages(part, all);
        return all;
    }

    public static void fillPages(NTxNode part, List<NTxNode> all) {
        switch (part.type()) {
            case NDocNodeType.PAGE: {
                NTxNode p = part;
                if (!p.isDisabled()) {
                    all.add(p);
                }
                break;
            }
            case NDocNodeType.PAGE_GROUP:
            case NDocNodeType.FLOW:
            case NDocNodeType.GRID:
            case NDocNodeType.GROUP: {
                if (!part.isDisabled()) {
                    for (NTxNode p : part.children()) {
                        fillPages(p, all);
                    }
                }
                break;
            }
        }
    }
}
