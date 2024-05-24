/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.text;

import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HLatex;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engine.nodes.AbstractHNode;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

/**
 * @author vpc
 */
public class HLatexImpl extends AbstractHNode implements HLatex {

    private String latex;

    public HLatexImpl() {
    }

    public HLatexImpl(String latex) {
        this.latex = latex;
    }

    @Override
    public String value() {
        return latex;
    }

    @Override
    public HNodeType type() {
        return HNodeType.LATEX;
    }

    public HLatex setValue(String latex) {
        this.latex = latex;
        return this;
    }

    @Override
    public void mergeNode(HItem other) {
        if (other != null) {
            super.mergeNode(other);
            if (other instanceof HLatex) {
                HLatex t = (HLatex) other;
                if (t.value() != null) {
                    this.latex = t.value();
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
