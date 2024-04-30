/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine;

import net.thevpc.halfa.api.model.AbstractHDocumentPart;
import net.thevpc.halfa.api.model.HDocumentItemType;
import net.thevpc.halfa.api.model.HText;

/**
 *
 * @author vpc
 */
public class DefaultHText extends AbstractHDocumentPart implements HText {

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
    public HDocumentItemType type() {
        return HDocumentItemType.TEXT;
    }

}
