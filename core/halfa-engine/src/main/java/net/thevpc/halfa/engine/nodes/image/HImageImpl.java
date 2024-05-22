/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.image;

import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.engine.nodes.AbstractHNode;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import java.awt.Image;


/**
 * @author vpc
 */
public class HImageImpl extends AbstractHNode implements HImage {

    private Image image;

    public HImageImpl() {
    }

    @Override
    public void mergeNode(HItem other) {
        if (other != null) {
            super.mergeNode(other);
            if (other instanceof HImage) {
                HImage t = (HImage) other;
                if (t.image() != null) {
                    this.image = t.image();
                }
            }
        }
    }

    public HImageImpl(Image image) {
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


    @Override
    public TsonElement toTson() {
        return ToTsonHelper.of(this)
                .addArg(Tson.string("some-path"))
                .build();
    }
}
