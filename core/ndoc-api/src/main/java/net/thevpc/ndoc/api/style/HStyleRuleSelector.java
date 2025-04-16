package net.thevpc.ndoc.api.style;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.nuts.elem.NToElement;

/**
 * TODO should add comparisons supporte!!
 */
public interface HStyleRuleSelector extends Comparable<HStyleRuleSelector>, NToElement {
    boolean acceptNode(HNode n);
}
