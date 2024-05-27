package net.thevpc.halfa.api.node;

import java.util.ArrayList;
import java.util.List;

public class HItemList implements HItem {
    private List<HItem> items = new ArrayList<>();

    public HItemList() {
        this.items = new ArrayList<>();
    }

    public HItemList add(HItem a) {
        if (a != null) {
            if (a instanceof HItemList) {
                for (HItem b : ((HItemList) a).getItems()) {
                    add(b);
                }
            } else {
                this.items.add(a);
            }
        }
        return this;
    }

    public HItemList addAll(List<HItem> all) {
        if (all != null) {
            for (HItem i : all) {
                add(i);
            }
        }
        return this;
    }

    public List<HItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "HItemList" + items;
    }

//    @Override
//    public TsonElement toTson() {
//        return Tson.obj(
//                items.stream().map(x -> x.toTson()).toArray(TsonElementBase[]::new)
//        ).build();
//    }
}
