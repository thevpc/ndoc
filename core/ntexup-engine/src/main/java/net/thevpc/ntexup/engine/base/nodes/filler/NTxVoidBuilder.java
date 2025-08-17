/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.base.nodes.filler;

import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.engine.NTxNodeBuilderContext;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;

/**
 * @author vpc
 */
public class NTxVoidBuilder implements NTxNodeBuilder {

    @Override
    public void build(NTxNodeBuilderContext builderContext) {
        builderContext.id(NTxNodeType.VOID)
                .parseDefaultParams()
                .renderComponent(this::rendererContext)
        ;
    }
    public void rendererContext(NTxNodeRendererContext rendererContext, NTxNodeBuilderContext builderContext) {

    }

//    NTxNodeType.VOID,
//    NTxNodeType.CTRL_ASSIGN,
//    NTxNodeType.CTRL_DEFINE
}
