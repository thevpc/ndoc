/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api.node;

import java.awt.geom.Point2D;

/**
 * @author vpc
 */
public interface HPolygon extends HNode {

    HPolygon add(Point2D.Double d);
    Point2D.Double[] points();

}
