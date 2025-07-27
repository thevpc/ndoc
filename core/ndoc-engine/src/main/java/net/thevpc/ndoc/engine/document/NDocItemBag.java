package net.thevpc.ndoc.api.util;

import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.node.NDocItemList;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeDef;
import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.document.style.NDocStyleRule;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.util.NMsg;

import java.util.ArrayList;
import java.util.List;

public class NDocItemBag {
    private List<NDocNode> nodes = new ArrayList<>();
    private List<NDocProp> props = new ArrayList<>();
    private List<NDocNodeDef> defs = new ArrayList<>();
    private List<NDocStyleRule> styleRules = new ArrayList<>();

    public NDocItemBag(List<NDocItem> nodes) {
        if(nodes!=null) {
            for (NDocItem node : nodes) {
                add(node);
            }
        }
    }

    public List<NDocNode> nodes() {
        return nodes;
    }

    public List<NDocProp> getProps() {
        return props;
    }

    public List<NDocNodeDef> getDefs() {
        return defs;
    }

    public List<NDocStyleRule> getStyleRules() {
        return styleRules;
    }

    private void add(NDocItem a) {
        if (a != null) {
            if (a instanceof NDocItemList) {
                for (NDocItem item : ((NDocItemList) a).getItems()) {
                    add(item);
                }
            }
            if (a instanceof NDocNode) {
                nodes.add((NDocNode) a);
                return;
            }
            if (a instanceof NDocProp) {
                props.add((NDocProp) a);
                return;
            }
            if (a instanceof NDocNodeDef) {
                defs.add((NDocNodeDef) a);
                return;
            }
            if (a instanceof NDocStyleRule) {
                styleRules.add((NDocStyleRule) a);
                return;
            }
            throw new NIllegalArgumentException(NMsg.ofC("unexpected item type " + a.getClass()));
        }
    }

    public NDocNode compress(boolean inPage) {

    }
    public boolean isNodes() {
        if(!props.isEmpty()){
            return false;
        }
        if(!defs.isEmpty()){
            return false;
        }
        if(!styleRules.isEmpty()){
            return false;
        }
        return true;
    }
}
