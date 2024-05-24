/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.filler;

import net.thevpc.halfa.api.model.XYConstraints;
import net.thevpc.halfa.api.node.HFiller;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HVoid;
import net.thevpc.halfa.engine.nodes.AbstractHNode;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import java.util.Objects;

/**
 * @author vpc
 */
public class HVoidImpl extends AbstractHNode implements HVoid {

    public HVoidImpl() {
    }

    @Override
    public HNodeType type() {
        return HNodeType.VOID;
    }

    @Override
    public String toString() {
        return "void";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HVoidImpl that = (HVoidImpl) o;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(0);
    }


    @Override
    public TsonElement toTson() {
        return Tson.name("void").build();
    }
}
