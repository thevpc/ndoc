/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.api.model.elem2d;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElements;
import net.thevpc.nuts.elem.NToElement;

/**
 * @author vpc
 */
public class HSize implements NToElement {

    private double value;
    private boolean percent;

    public static HSize ofPercent(double value) {
        return new HSize(value, true);
    }

    public static HSize of(double value) {
        return new HSize(value, false);
    }

    public HSize(double value, boolean percent) {
        this.value = value;
        this.percent = percent;
    }

    @Override
    public NElement toElement() {
        return NElements.of().ofNumber(value + "%");
    }
}
