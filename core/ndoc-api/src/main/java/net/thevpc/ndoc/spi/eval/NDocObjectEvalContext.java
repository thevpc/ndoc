/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.spi.eval;

import net.thevpc.tson.TsonElement;

/**
 * @author vpc
 */
public interface NDocObjectEvalContext {

    TsonElement eval(TsonElement other);
}
