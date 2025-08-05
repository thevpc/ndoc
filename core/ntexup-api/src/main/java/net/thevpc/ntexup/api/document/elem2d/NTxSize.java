/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.api.document.elem2d;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NToElement;

/**
 * @author vpc
 */
public class NTxSize implements NToElement {

    private double value;
    private boolean percent;

    public static NTxSize ofPercent(double value) {
        return new NTxSize(value, true);
    }

    public static NTxSize of(double value) {
        return new NTxSize(value, false);
    }

    public NTxSize(double value, boolean percent) {
        this.value = value;
        this.percent = percent;
    }

    @Override
    public NElement toElement() {
        return NElement.ofNumber(value + "%");
    }
}
