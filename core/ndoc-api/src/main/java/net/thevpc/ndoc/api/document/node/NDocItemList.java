package net.thevpc.ndoc.api.document.node;

import net.thevpc.ndoc.api.source.NDocResource;

import java.util.ArrayList;
import java.util.List;

public class NDocItemList implements NDocItem {
    private List<NDocItem> items = new ArrayList<>();

    @Override
    public NDocItem parent() {
        return null;
    }

    @Override
    public NDocResource source() {
        return null;
    }

    public NDocItemList() {
        this.items = new ArrayList<>();
    }

    public NDocItemList add(NDocItem a) {
        if (a != null) {
            if (a instanceof NDocItemList) {
                for (NDocItem b : ((NDocItemList) a).getItems()) {
                    add(b);
                }
            } else {
                this.items.add(a);
            }
        }
        return this;
    }

    public NDocItemList addAll(List<NDocItem> all) {
        if (all != null) {
            for (NDocItem i : all) {
                add(i);
            }
        }
        return this;
    }

    public List<NDocItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "HItemList" + items;
    }

//    @Override
//    public NElement toElement() {
//        return NElements.obj(
//                items.stream().map(x -> x.toElement()).toArray(NElement[]::new)
//        ).build();
//    }
}
