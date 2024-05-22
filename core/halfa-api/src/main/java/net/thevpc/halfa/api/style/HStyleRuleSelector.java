package net.thevpc.halfa.api.style;

import net.thevpc.halfa.api.node.HNode;

import java.util.function.Predicate;

/**
 * TODO should add comparisons supporte!!
 */
public interface HStyleRuleSelector extends Predicate<HNode> , Comparable<HStyleRuleSelector>{
}
