/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api.node;

import net.thevpc.halfa.api.model.Double2;

/**
 * @author vpc
 */
public interface HPolyline extends HNode {

    Double2[] points();
    HPolyline add(Double2 d);
}
