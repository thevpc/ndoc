/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.filler;

import net.thevpc.halfa.api.model.*;
import net.thevpc.halfa.api.node.HFiller;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engine.nodes.AbstractHNode;

import java.util.Objects;

/**
 * @author vpc
 */
public class HFillerImpl extends AbstractHNode implements HFiller {
    private XYConstraints constraints;

    public HFillerImpl(XYConstraints constraints) {
        this.constraints = constraints;
    }

    public XYConstraints getConstraints() {
        return constraints;
    }

    @Override
    public HNodeType type() {
        return HNodeType.FILLER;
    }

    @Override
    public String toString() {
        return "Filler{" + constraints + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HFillerImpl that = (HFillerImpl) o;
        return Objects.equals(constraints, that.constraints);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(constraints);
    }
}
