/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine;

import net.thevpc.halfa.api.model.*;

import java.util.List;

/**
 * @author vpc
 */
public class DefaultHContainer extends AbstractHPagePart implements HContainer {

    private List<HPagePart> children;

    public DefaultHContainer(List<HPagePart> children) {
        this.children = children;
    }

    @Override
    public HContainer add(HPagePart a) {
        if (a != null) {
            children.add(a);
        }
        return this;
    }

    @Override
    public List<HPagePart> children() {
        return children;
    }

    @Override
    public HDocumentItemType type() {
        return HDocumentItemType.CONTAINER;
    }

    @Override
    public HContainer unset(HStyleType s) {
        super.unset(s);
        return this;
    }

    @Override
    public HContainer set(HStyle s) {
        super.set(s);
        return this;
    }
}
