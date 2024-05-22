/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.container;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.container.HStackContainer;

import java.util.List;

/**
 * @author vpc
 */
public class HStackContainerImpl extends AbstractHContainer implements HStackContainer {

    public HStackContainerImpl(List<HNode> children) {
        super(children);
    }

    public HNodeType type() {
        return HNodeType.STACK;
    }

}
