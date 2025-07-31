/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.filler;

import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.api.document.node.NDocNodeType;

/**
 * @author vpc
 */
public class NDocVoidBuilder extends NDocNodeParserBase {

    public NDocVoidBuilder() {
        super(false, NDocNodeType.VOID);
    }
}
