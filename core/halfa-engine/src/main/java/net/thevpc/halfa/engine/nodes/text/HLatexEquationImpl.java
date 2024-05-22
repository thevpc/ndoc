/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.text;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HLatexEquation;
import net.thevpc.halfa.engine.nodes.AbstractHNode;

/**
 *
 * @author vpc
 */
public class HLatexEquationImpl extends AbstractHNode implements HLatexEquation {

    private String latex;

    public HLatexEquationImpl() {
    }

    public HLatexEquationImpl(String latex) {
        this.latex = latex;
    }

    @Override
    public String latex() {
        return latex;
    }

    @Override
    public HNodeType type() {
        return HNodeType.EQUATION;
    }

}
