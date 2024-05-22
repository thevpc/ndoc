/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api.node;

import java.awt.geom.Point2D;

/**
 * @author vpc
 */
public interface HLine extends HNode {
    Point2D.Double from();
    Point2D.Double to();
    HLine setFrom(Point2D.Double from);

    HLine setTo(Point2D.Double to);
}
