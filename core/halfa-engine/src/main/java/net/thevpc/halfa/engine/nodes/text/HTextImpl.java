/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.text;

import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HText;
import net.thevpc.halfa.engine.nodes.AbstractHNode;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

/**
 * @author vpc
 */
public class HTextImpl extends AbstractHNode implements HText {

    private String message;

    public HTextImpl() {
    }

    public HTextImpl(String message) {
        this.message = message;
    }

    @Override
    public String value() {
        return message;
    }

    @Override
    public HText setValue(String message) {
        this.message = message;
        return this;
    }

    @Override
    public HNodeType type() {
        return HNodeType.TEXT;
    }

    @Override
    public String toString() {
        return "DefaultHText{" +
                "message='" + message + '\'' +
                '}';
    }

    @Override
    public void mergeNode(HItem other) {
        if (other != null) {
            super.mergeNode(other);
            if (other instanceof HText) {
                HText t = (HText) other;
                if (t.value() != null) {
                    this.message = t.value();
                }
            }
        }
    }

    @Override
    public TsonElement toTson() {
        return ToTsonHelper.of(this)
                .addArg(message == null ? null : Tson.string(message))
                .build();
    }

}
