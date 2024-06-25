/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api.style;

import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.tson.TsonElement;

/**
 * @author vpc
 */
public interface HStyleRule extends HItem {
    boolean accept(HNode node);

    HStyleRuleSelector selector();

    HProperties styles();

    HStyleRuleResult styles(HNode node);

    TsonElement toTson();

}
