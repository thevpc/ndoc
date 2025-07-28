/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.container.flow;

import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;

/**
 * @author vpc
 */
public class NDocFlowContainerParser extends NDocNodeParserBase {
    public NDocFlowContainerParser() {
        super(true, NDocNodeType.FLOW);
    }
}
