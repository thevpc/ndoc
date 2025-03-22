/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.filler;

import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;

/**
 * @author vpc
 */
public class NDocVoidParser extends NDocNodeParserBase {

    public NDocVoidParser() {
        super(false, HNodeType.VOID);
    }
}
