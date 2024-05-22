/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.image;

import net.thevpc.halfa.api.node.HImage;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engine.nodes.AbstractHNode;

import java.awt.*;

/**
 *
 * @author vpc
 */
public class DefaultHImage extends AbstractHNode implements HImage {

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
    public HNodeType type() {
        return HNodeType.IMAGE;
    }

}
