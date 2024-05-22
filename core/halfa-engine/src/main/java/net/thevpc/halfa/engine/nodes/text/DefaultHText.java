/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.text;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HText;
import net.thevpc.halfa.engine.nodes.AbstractHNode;

/**
 *
 * @author vpc
 */
public class DefaultHText extends AbstractHNode implements HText {

    private String message;

    public DefaultHText() {
    }

    public DefaultHText(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
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
}
