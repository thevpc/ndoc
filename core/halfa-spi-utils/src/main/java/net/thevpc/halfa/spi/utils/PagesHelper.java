package net.thevpc.halfa.spi.utils;

import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HPage;

import java.util.ArrayList;
import java.util.List;

public class PagesHelper {
    public static List<HPage> resolvePages(HDocument document) {
        List<HPage> all = new ArrayList<>();
        fillPages(document.root(), all);
        return all;
    }

    public static List<HPage> resolvePages(HNode part) {
        List<HPage> all = new ArrayList<>();
        fillPages(part, all);
        return all;
    }

    public static void fillPages(HNode part, List<HPage> all) {
        switch (part.type()) {
            case PAGE: {
                HPage p = (HPage) part;
                if (!p.isTemplate()) {
                    if (!p.isDisabled()) {
                        all.add(p);
                    }
                }
                break;
            }
            case PAGE_GROUP:
            case FLOW:
            case GRID:
            case STACK: {
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
