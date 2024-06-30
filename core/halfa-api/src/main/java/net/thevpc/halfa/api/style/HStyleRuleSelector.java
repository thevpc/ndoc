package net.thevpc.halfa.api.style;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.tson.ToTson;

import java.util.function.Predicate;

/**
 * TODO should add comparisons supporte!!
 */
public interface HStyleRuleSelector extends Predicate<HNode>, Comparable<HStyleRuleSelector>, ToTson {
}
