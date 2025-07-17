/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.engine.parser.nodes;

import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;

/**
 * @author vpc
 */
public class NDocPageGroupParser extends NDocNodeParserBase {

    public NDocPageGroupParser() {
        super(true, NDocNodeType.PAGE_GROUP);
    }
}
