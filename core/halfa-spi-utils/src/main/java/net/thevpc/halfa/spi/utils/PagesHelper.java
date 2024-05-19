package net.thevpc.halfa.spi.utils;

import net.thevpc.halfa.api.model.*;

import java.util.ArrayList;
import java.util.List;

public class PagesHelper {
    public static List<HPage> resolvePages(HDocument document) {
        List<HPage> all = new ArrayList<>();
        for (HDocumentPart p : document.getDocumentParts()) {
            fillPages(p, all);
        }
        return all;
    }

    public static List<HPage> resolvePages(HDocumentItem part) {
        List<HPage> all = new ArrayList<>();
        fillPages(part, all);
        return all;
    }

    public static void fillPages(HDocumentItem part, List<HPage> all) {
        switch (part.type()) {
            case PAGE: {
                all.add((HPage) part);
                break;
            }
            case PAGE_GROUP: {
                for (HDocumentPart p : ((HPageGroup) part).getDocumentParts()) {
                    fillPages(p, all);
                }
                break;
            }
        }
    }
}
