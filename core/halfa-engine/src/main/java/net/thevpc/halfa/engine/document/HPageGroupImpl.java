/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.document;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engine.nodes.AbstractHNodeTypeFactory;

/**
 * @author vpc
 */
public class HPageGroupImpl extends AbstractHNodeTypeFactory {

    public HPageGroupImpl() {
        super(true, HNodeType.PAGE_GROUP);
    }
}
