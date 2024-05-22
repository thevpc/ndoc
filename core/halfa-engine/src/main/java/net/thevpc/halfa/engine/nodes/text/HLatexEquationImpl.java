/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.text;

import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.engine.nodes.AbstractHNode;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

/**
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

    public HLatexEquation setLatex(String latex) {
        this.latex = latex;
        return this;
    }

    @Override
    public void mergeNode(HItem other) {
        if (other != null) {
            super.mergeNode(other);
            if (other instanceof HLatexEquation) {
                HLatexEquation t = (HLatexEquation) other;
                if (t.latex() != null) {
                    this.latex = t.latex();
                }
            }
        }
    }

    @Override
    public TsonElement toTson() {
        return ToTsonHelper.of(this)
                .addArg(latex == null ? null : Tson.string(latex))
                .build();
    }

}
