/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api.model;

/**
 *
 * @author vpc
 */
public abstract class AbstractHPagePart extends AbstractHDocumentPart implements HPagePart {

    public HPagePart set(HStyle s) {
        super.set(s);
        return this;
    }

    public HPagePart unset(HStyleType s) {
        super.unset(s);
        return this;
    }
}
