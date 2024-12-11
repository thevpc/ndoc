/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.examples;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.io.NPath;

/**
 * @author vpc
 */
public class HalfaExampleFromFolder {

    public static void main(String[] args) {
        Nuts.openWorkspace().setSharedInstance();
        HEngine e = new HEngineImpl();
        NPath file = NPath.of("C:\\Users\\ibtih\\IdeaProjects\\halfa\\test\\halfa-examples\\src\\halfa\\ibtihel").toAbsolute().normalize();
        e.newScreenRenderer().renderPath(file);
    }
}
