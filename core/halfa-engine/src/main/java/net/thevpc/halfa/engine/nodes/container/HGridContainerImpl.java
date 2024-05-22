/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.container;

import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.api.model.*;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.container.HGridContainer;
import net.thevpc.halfa.api.node.HNode;

import java.util.List;

/**
 * @author vpc
 */
public class HGridContainerImpl extends AbstractHContainer implements HGridContainer {

    public HGridContainerImpl(List<HNode> children) {
        super(children);
    }

    public HNodeType type() {
        return HNodeType.GRID;
    }

}
