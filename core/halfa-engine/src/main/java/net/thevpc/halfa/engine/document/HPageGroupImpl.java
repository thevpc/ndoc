/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.document;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HPageGroup;
import net.thevpc.halfa.engine.nodes.container.AbstractHContainer;

/**
 * @author vpc
 */
public class HPageGroupImpl extends AbstractHContainer implements HPageGroup {

    public HPageGroupImpl() {
        super();
    }

    public HNodeType type() {
        return HNodeType.PAGE_GROUP;
    }

}
