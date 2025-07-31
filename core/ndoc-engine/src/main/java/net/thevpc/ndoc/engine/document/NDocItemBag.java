package net.thevpc.ndoc.engine.document;

import net.thevpc.ndoc.api.document.node.*;
import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.document.style.NDocStyleRule;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.util.NMsg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NDocItemBag {
    private List<NDocNode> nodes = new ArrayList<>();
    private List<NDocProp> props = new ArrayList<>();
    private List<NDocNodeDef> defs = new ArrayList<>();
    private List<NDocStyleRule> styleRules = new ArrayList<>();
    private List<NDocItem> list = new ArrayList<>();

    public NDocItemBag(List<NDocItem> nodes) {
        if (nodes != null) {
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
                list.add(a);
                return;
            }
            if (a instanceof NDocProp) {
                props.add((NDocProp) a);
                list.add(a);
                return;
            }
            if (a instanceof NDocNodeDef) {
                defs.add((NDocNodeDef) a);
                list.add(a);
                return;
            }
            if (a instanceof NDocStyleRule) {
                styleRules.add((NDocStyleRule) a);
                list.add(a);
                return;
            }
            throw new NIllegalArgumentException(NMsg.ofC("unexpected item type " + a.getClass()));
        }
    }

    public List<NDocNode> compressToNodes(boolean inPage, NDocResource source) {
        if(isNodes()){
            return new ArrayList<>(nodes());
        }
        return new ArrayList<>(Arrays.asList(compress(inPage,source)));
    }

    public NDocNode compress(boolean inPage, NDocResource source) {
        if (isEmpty()) {
            return new DefaultNDocNode(NDocNodeType.VOID, source);
        } else if (list.size() == 1 && list.get(0) instanceof NDocNode) {
            return (NDocNode) list.get(0);
        } else {
            if (inPage) {
                DefaultNDocNode root = new DefaultNDocNode(NDocNodeType.GROUP, source);
                for (NDocItem nDocItem : list) {
                    root.mergeNode(nDocItem);
                }
                return root;
            } else {
                DefaultNDocNode root = new DefaultNDocNode(NDocNodeType.PAGE_GROUP, source);
                for (NDocItem nDocItem : list) {
                    root.mergeNode(nDocItem);
                }
                return root;
            }
        }
    }

    public List<NDocItem> all() {
        return list;
    }
    public boolean isEmpty() {
        if (!nodes.isEmpty()) {
            return false;
        }
        if (!props.isEmpty()) {
            return false;
        }
        if (!defs.isEmpty()) {
            return false;
        }
        if (!styleRules.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean isNodes() {
        if (!props.isEmpty()) {
            return false;
        }
        if (!defs.isEmpty()) {
            return false;
        }
        if (!styleRules.isEmpty()) {
            return false;
        }
        return true;
    }
}
