/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api.model.elem2d;

/**
 * @author vpc
 */
public class HSize {

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

}
