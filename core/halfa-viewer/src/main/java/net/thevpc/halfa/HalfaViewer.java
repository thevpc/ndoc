/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa;

import com.formdev.flatlaf.FlatLightLaf;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.halfa.debug.HDebugFrame;
import net.thevpc.halfa.main.MainFrame;
import net.thevpc.halfa.spi.renderer.HDocumentScreenRenderer;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NStringUtils;

import javax.swing.*;
import java.io.File;

/**
 * @author vpc
 */
public class HalfaViewer {



    public static void main(String[] args) {
        FlatLightLaf.setup(new com.formdev.flatlaf.FlatDarculaLaf());
        NSession session = Nuts.openWorkspace();
        MainFrame mainFrame=new MainFrame(session);
        mainFrame.setVisible(true);

    }
}
