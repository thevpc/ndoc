/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api.node;

import java.awt.geom.Point2D;

/**
 * @author vpc
 */
public interface HPolyline extends HNode {

    Point2D.Double[] points();
    HPolyline add(Point2D.Double d);
}
