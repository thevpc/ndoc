/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.spi.util;

import net.thevpc.tson.TsonElement;

/**
 *
 * @author vpc
 */
public interface ObjectEvalContext {

    TsonElement eval(TsonElement other);
}
