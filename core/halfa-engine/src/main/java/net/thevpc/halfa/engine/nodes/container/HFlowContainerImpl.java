/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.container;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.container.HFlowContainer;
import net.thevpc.halfa.api.node.HNode;

import java.util.List;

/**
 * @author vpc
 */
public class HFlowContainerImpl extends AbstractHContainer implements HFlowContainer {

    public HFlowContainerImpl(List<HNode> children) {
        super(children);
    }

    public HNodeType type() {
        return HNodeType.FLOW;
    }

}
