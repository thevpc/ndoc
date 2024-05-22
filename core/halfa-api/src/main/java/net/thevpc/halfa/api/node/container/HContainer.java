/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api.node.container;

import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.api.style.HStyleType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HStyleRule;

import java.util.List;

/**
 * @author vpc
 */
public interface HContainer extends HNode {


    List<HNode> children();

    HContainer setName(String name);

    HContainer add(HNode a);

    HContainer set(HStyle s);

    HContainer set(HStyle... s);

    HContainer addRule(HStyleRule s);

    HContainer removeRule(HStyleRule s);

    HContainer addRule(HStyle... s);

    HContainer addRules(HStyleRule... s);

    HContainer clearRules();

    HStyleRule[] rules();

    HContainer unset(HStyleType s);

}
