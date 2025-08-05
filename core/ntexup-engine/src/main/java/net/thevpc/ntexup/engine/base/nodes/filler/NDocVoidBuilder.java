/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.base.nodes.filler;

import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.extension.NDocNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;

/**
 * @author vpc
 */
public class NDocVoidBuilder implements NDocNodeBuilder {

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.VOID)
                .parseDefaultParams()
        ;
    }

//    NDocNodeType.VOID,
//    NDocNodeType.CTRL_ASSIGN,
//    NDocNodeType.CTRL_DEFINE
}
