/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api.model;

/**
 *
 * @author vpc
 */
public interface HPagePart extends HDocumentItem{
    HPagePart set(HStyle s);

    HPagePart unset(HStyleType s);

}
