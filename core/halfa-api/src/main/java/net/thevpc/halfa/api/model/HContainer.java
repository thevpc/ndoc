/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api.model;

import java.util.List;

/**
 * @author vpc
 */
public interface HContainer extends HPagePart {

    List<HPagePart> children();

    HContainer add(HPagePart a);

    HContainer set(HStyle s);

    HContainer unset(HStyleType s);

}
