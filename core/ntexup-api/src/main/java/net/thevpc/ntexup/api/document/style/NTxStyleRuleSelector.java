package net.thevpc.ntexup.api.document.style;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.nuts.elem.NToElement;

/**
 * TODO should add comparisons supporte!!
 */
public interface NTxStyleRuleSelector extends Comparable<NTxStyleRuleSelector>, NToElement {
    boolean acceptNode(NTxNode n);
}
