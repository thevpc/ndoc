/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine;

import net.thevpc.halfa.api.model.AbstractHPagePart;
import net.thevpc.halfa.api.model.HDocumentItemType;
import net.thevpc.halfa.api.model.HImage;
import net.thevpc.halfa.api.model.HLatexEquation;

import java.awt.*;

/**
 *
 * @author vpc
 */
public class DefaultHImage extends AbstractHPagePart implements HImage {

    private Image image;

    public DefaultHImage() {
    }

    public DefaultHImage(Image image) {
        this.image = image;
    }

    @Override
    public Image image() {
        return image;
    }

    @Override
    public HDocumentItemType type() {
        return HDocumentItemType.IMAGE;
    }

}
