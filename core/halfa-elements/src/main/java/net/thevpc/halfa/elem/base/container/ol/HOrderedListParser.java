/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.elem.base.container.ol;

import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;

/**
 * @author vpc
 */
public class HOrderedListParser extends HNodeParserBase {
    public HOrderedListParser() {
        super(true, HNodeType.ORDERED_LIST, "ol");
    }

}
