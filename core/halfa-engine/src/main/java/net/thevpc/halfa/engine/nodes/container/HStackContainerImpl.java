/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.container;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engin.spibase.parser.AbstractHNodeTypeFactory;

/**
 * @author vpc
 */
public class HStackContainerImpl extends AbstractHNodeTypeFactory {
    public HStackContainerImpl() {
        super(true, HNodeType.STACK);
    }

}
