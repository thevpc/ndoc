/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.engine.parser.nodeparsers;

import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.engine.parser.NDocNodeParserBase;

/**
 * @author vpc
 */
public class NDocPageGroupParser extends NDocNodeParserBase {

    public NDocPageGroupParser() {
        super(true, NDocNodeType.PAGE_GROUP);
    }
}
