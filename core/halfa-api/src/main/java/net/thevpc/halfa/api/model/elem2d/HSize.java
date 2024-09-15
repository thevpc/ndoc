/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api.model.elem2d;

import net.thevpc.tson.ToTson;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

/**
 * @author vpc
 */
public class HSize implements ToTson {

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
    public TsonElement toTson() {
        return Tson.parseNumber(value + "%");
    }
}
