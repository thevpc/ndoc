package net.thevpc.halfa.spi.utils;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.node.HNode;

import java.util.ArrayList;
import java.util.List;

public class PagesHelper {
    public static List<HNode> resolvePages(HDocument document) {
        List<HNode> all = new ArrayList<>();
        fillPages(document.root(), all);
        return all;
    }

    public static List<HNode> resolvePages(HNode part) {
        List<HNode> all = new ArrayList<>();
        fillPages(part, all);
        return all;
    }

    public static void fillPages(HNode part, List<HNode> all) {
        switch (part.type()) {
            case HNodeType.PAGE: {
                HNode p = (HNode) part;
                if (!p.isTemplate()) {
                    if (!p.isDisabled()) {
                        all.add(p);
                    }
                }
                break;
            }
            case HNodeType.PAGE_GROUP:
            case HNodeType.FLOW:
            case HNodeType.GRID:
            case HNodeType.STACK: {
                if (!part.isTemplate()) {
                    if (!part.isDisabled()) {
                        for (HNode p : ((HContainer) part).children()) {
                            fillPages(p, all);
                        }
                    }
                }
                break;
            }
        }
    }
}
