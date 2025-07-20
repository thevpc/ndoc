/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.spi.eval;

import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.nuts.elem.NElement;

/**
 * @author vpc
 */
public interface NDocObjectEvalContext {

    NElement eval(NDocNode node, NElement other);
}
