/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.elem.base.control;

import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.tson.Tson;

/**
 * @author vpc
 */
public class HElseIfParser extends HNodeParserBase {
    public HElseIfParser() {
        super(true, "elseif");
    }

    protected boolean processArguments(ParseArgumentInfo info) {
        if(info.arguments.length==1){
            info.node.setProperty("condition", info.arguments[0]);
        }else{
            info.node.setProperty("condition", Tson.ofArray(info.arguments).build());
        }
        return true;
    }
}
