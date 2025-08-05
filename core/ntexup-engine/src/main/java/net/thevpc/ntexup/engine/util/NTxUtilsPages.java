package net.thevpc.ntexup.engine.util;

import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.document.NTxDocument;
import net.thevpc.ntexup.api.document.node.NTxNode;

import java.util.ArrayList;
import java.util.List;

public class NTxUtilsPages {
    public static List<NTxNode> resolvePages(NTxDocument document) {
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
            case NTxNodeType.PAGE: {
                NTxNode p = part;
                if (!p.isDisabled()) {
                    all.add(p);
                }
                break;
            }
            case NTxNodeType.PAGE_GROUP:
            case NTxNodeType.FLOW:
            case NTxNodeType.GRID:
            case NTxNodeType.GROUP: {
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
