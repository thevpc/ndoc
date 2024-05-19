/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine;

import net.thevpc.halfa.api.model.AbstractHPagePart;
import net.thevpc.halfa.api.model.HDocumentItemType;
import net.thevpc.halfa.api.model.HLatexEquation;

/**
 *
 * @author vpc
 */
public class DefaultHLatexEquation extends AbstractHPagePart implements HLatexEquation {

    private String latex;

    public DefaultHLatexEquation() {
    }

    public DefaultHLatexEquation(String latex) {
        this.latex = latex;
    }

    @Override
    public String latex() {
        return latex;
    }

    @Override
    public HDocumentItemType type() {
        return HDocumentItemType.LATEX_EQUATION;
    }

}
