package net.thevpc.ndoc.api.document.style;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.nuts.elem.NToElement;

/**
 * TODO should add comparisons supporte!!
 */
public interface NDocStyleRuleSelector extends Comparable<NDocStyleRuleSelector>, NToElement {
    boolean acceptNode(NDocNode n);
}
