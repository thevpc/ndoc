/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.container;

import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.container.HUnorderedList;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.api.style.HStyleType;

import java.util.List;

/**
 * @author vpc
 */
public class HUnorderedListImpl extends AbstractHContainer implements HUnorderedList {

    public HUnorderedListImpl(List<HNode> children) {
        super(children);
    }

    @Override
    public HUnorderedList add(HNode a) {
        super.add(a);
        return this;
    }

    public HNodeType type() {
        return HNodeType.UNORDERED_LIST;
    }

    @Override
    public HUnorderedList unset(HStyleType s) {
        super.unset(s);
        return this;
    }

    @Override
    public HUnorderedList set(HStyle s) {
        super.set(s);
        return this;
    }

}
