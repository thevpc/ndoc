/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.elem.base.control;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

/**
 * @author vpc
 */
public class HIfParser extends HNodeParserBase {
    public HIfParser() {
        super(true, HNodeType.IF);
    }

    protected boolean processArguments(String id, TsonElement tsonElement, HNode node, TsonElement[] arguments, HDocumentFactory f, HNodeFactoryParseContext context) {
        if(arguments.length==1){
            node.setProperty("condition", arguments[0]);
        }else{
            node.setProperty("condition", Tson.ofArray(arguments).build());
        }
        return true;
    }
}
