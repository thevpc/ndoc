/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api.node;

import net.thevpc.halfa.api.model.Double2;

/**
 * @author vpc
 */
public interface HLine extends HNode {
    Double2 from();
    Double2 to();
    HLine setFrom(Double2 from);

    HLine setTo(Double2 to);
}
