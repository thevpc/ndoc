/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.parser.nodeparsers;

import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.engine.parser.NDocNodeParserBase;

/**
 * @author vpc
 */
public class NDocPageGroupParser extends NDocNodeParserBase {

    public NDocPageGroupParser() {
        super(true, NTxNodeType.PAGE_GROUP);
    }
}
