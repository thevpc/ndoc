/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.control;

import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.nuts.elem.NElements;

/**
 * @author vpc
 */
public class NDocIfParser extends NDocNodeParserBase {
    public NDocIfParser() {
        super(true, HNodeType.IF);
    }

    protected boolean processArguments(ParseArgumentInfo info) {
        if(info.arguments.length==1){
            info.node.setProperty("condition", info.arguments[0]);
        }else{
            info.node.setProperty("condition", NElements.ofArray(info.arguments));
        }
        return true;
    }
}
