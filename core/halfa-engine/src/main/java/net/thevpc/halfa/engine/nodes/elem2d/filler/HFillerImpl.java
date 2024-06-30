/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.elem2d.filler;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.engin.spibase.parser.HNodeParserBase;

/**
 * @author vpc
 */
public class HFillerImpl extends HNodeParserBase {
    public HFillerImpl() {
        super(false, HNodeType.FILLER);
    }
}
