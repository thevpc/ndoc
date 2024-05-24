/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.container;

import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HOrderedList;

import java.util.List;

/**
 * @author vpc
 */
public class HOrderedListImpl extends AbstractHContainer implements HOrderedList {

    public HOrderedListImpl(List<HNode> children) {
        super(children);
    }

    public HNodeType type() {
        return HNodeType.ORDERED_LIST;
    }


}
