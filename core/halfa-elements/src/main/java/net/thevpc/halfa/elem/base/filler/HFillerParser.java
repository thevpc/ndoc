/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.elem.base.filler;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;

/**
 * @author vpc
 */
public class HFillerParser extends HNodeParserBase {
    public HFillerParser() {
        super(false, HNodeType.FILLER);
    }
}
