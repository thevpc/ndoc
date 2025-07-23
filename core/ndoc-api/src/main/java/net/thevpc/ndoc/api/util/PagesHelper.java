package net.thevpc.ndoc.api.util;

import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.node.NDocNode;

import java.util.ArrayList;
import java.util.List;

public class PagesHelper {
    public static List<NDocNode> resolvePages(NDocument document) {
        List<NDocNode> all = new ArrayList<>();
        fillPages(document.root(), all);
        return all;
    }

    public static List<NDocNode> resolvePages(NDocNode part) {
        List<NDocNode> all = new ArrayList<>();
        fillPages(part, all);
        return all;
    }

    public static void fillPages(NDocNode part, List<NDocNode> all) {
        switch (part.type()) {
            case NDocNodeType.PAGE: {
                NDocNode p = part;
                if (!p.isDisabled()) {
                    all.add(p);
                }
                break;
            }
            case NDocNodeType.PAGE_GROUP:
            case NDocNodeType.FLOW:
            case NDocNodeType.GRID:
            case NDocNodeType.STACK: {
                if (!part.isDisabled()) {
                    for (NDocNode p : part.children()) {
                        fillPages(p, all);
                    }
                }
                break;
            }
        }
    }
}
