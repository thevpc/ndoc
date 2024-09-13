package net.thevpc.halfa.api.style;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.tson.ToTson;

/**
 * TODO should add comparisons supporte!!
 */
public interface HStyleRuleSelector extends Comparable<HStyleRuleSelector>, ToTson {
    boolean acceptNode(HNode n);
}
