/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.filler;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engin.spibase.parser.AbstractHNodeTypeFactory;

/**
 * @author vpc
 */
public class HFillerImpl extends AbstractHNodeTypeFactory {
    public HFillerImpl() {
        super(false, HNodeType.FILLER);
    }
}
