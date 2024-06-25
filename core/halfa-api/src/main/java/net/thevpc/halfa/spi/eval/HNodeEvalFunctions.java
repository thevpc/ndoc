/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.spi.eval;

import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

/**
 * @author vpc
 */
public interface HNodeEvalFunctions {

    public NOptional<TsonElement> evalFunction(String varName, TsonElement[] vars);

}
