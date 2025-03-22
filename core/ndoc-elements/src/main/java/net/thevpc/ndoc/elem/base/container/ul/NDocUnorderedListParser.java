/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.container.ul;

import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;

/**
 * @author vpc
 */
public class NDocUnorderedListParser extends NDocNodeParserBase {
    public NDocUnorderedListParser() {
        super(true, HNodeType.UNORDERED_LIST, "ul");
    }


}
