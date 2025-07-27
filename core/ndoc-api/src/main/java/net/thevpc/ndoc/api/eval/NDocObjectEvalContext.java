/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.api.eval;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

/**
 * @author vpc
 */
public interface NDocObjectEvalContext {

    NOptional<NDocVar> findVar(String varName, NDocNode node);

    NElement eval(NElement elementExpr, NDocNode node);

}
