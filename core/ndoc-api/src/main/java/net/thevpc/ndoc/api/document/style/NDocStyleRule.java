/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.api.document.style;

import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.nuts.elem.NElement;

/**
 * @author vpc
 */
public interface NDocStyleRule extends NDocItem {
    boolean acceptNode(NDocNode node);

    NDocStyleRuleSelector selector();

    NDocProperties styles();

    HStyleRuleResult styles(NDocNode node);

    NElement toElement();

}
