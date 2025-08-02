/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.engine.base.nodes.filler;

import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.extension.NDocNodeBuilder;
import net.thevpc.ndoc.api.engine.NDocNodeCustomBuilderContext;

/**
 * @author vpc
 */
public class NDocVoidBuilder implements NDocNodeBuilder {

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(NDocNodeType.VOID)
                .parseDefaultParams()
        ;
    }

//    NDocNodeType.VOID,
//    NDocNodeType.CTRL_ASSIGN,
//    NDocNodeType.CTRL_DEFINE
}
