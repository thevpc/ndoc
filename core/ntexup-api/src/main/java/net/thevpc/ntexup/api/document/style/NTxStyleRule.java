/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.api.document.style;

import net.thevpc.ntexup.api.document.node.NTxItem;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.nuts.elem.NElement;

/**
 * @author vpc
 */
public interface NTxStyleRule extends NTxItem {
    boolean acceptNode(NTxNode node);

    NTxStyleRuleSelector selector();

    NTxProperties styles();

    NTxStyleRuleResult styles(NTxNode node);

    NElement toElement();

}
