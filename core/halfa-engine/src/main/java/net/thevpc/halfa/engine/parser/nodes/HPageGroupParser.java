/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.parser.nodes;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;

/**
 * @author vpc
 */
public class HPageGroupParser extends HNodeParserBase {

    public HPageGroupParser() {
        super(true, HNodeType.PAGE_GROUP);
    }
}
