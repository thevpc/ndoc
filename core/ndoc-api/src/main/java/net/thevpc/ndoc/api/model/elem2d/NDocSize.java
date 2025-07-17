/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.api.model.elem2d;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NToElement;

/**
 * @author vpc
 */
public class NDocSize implements NToElement {

    private double value;
    private boolean percent;

    public static NDocSize ofPercent(double value) {
        return new NDocSize(value, true);
    }

    public static NDocSize of(double value) {
        return new NDocSize(value, false);
    }

    public NDocSize(double value, boolean percent) {
        this.value = value;
        this.percent = percent;
    }

    @Override
    public NElement toElement() {
        return NElement.ofNumber(value + "%");
    }
}
