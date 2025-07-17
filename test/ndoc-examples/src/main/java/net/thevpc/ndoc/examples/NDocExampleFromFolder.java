/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.examples;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.engine.DefaultNDocEngine;
import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.io.NPath;

/**
 * @author vpc
 */
public class NDocExampleFromFolder {

    public static void main(String[] args) {
        Nuts.openWorkspace().share();
        NDocEngine e = new DefaultNDocEngine();
        NPath file = NPath.of("C:\\Users\\ibtih\\IdeaProjects\\ndoc\\test\\halfa-examples\\src\\ndoc\\ibtihel").toAbsolute().normalize();
        e.newScreenRenderer().get().renderPath(file);
    }
}
