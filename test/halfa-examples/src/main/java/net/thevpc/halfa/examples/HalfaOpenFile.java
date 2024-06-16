/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.examples;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.halfa.engine.renderer.screen.debug.HDebugFrame;
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
public class HalfaOpenFile {

    private static NPath confPath(NSession session) {
        NPath c = session.getAppConfFolder();
        if (c == null) {
            c = NPath.ofUserHome(session).resolve(".config");
        }
        return c.resolve("HalfaOpenFile.conf");
    }

    private static NPath loadPath(NSession session) {
        try {
            String s = NStringUtils.trimToNull(confPath(session).readString());
            if (s != null) {
                return NPath.of(s, session);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static void savePath(NPath path, NSession session) {
        try {
            NPath p = confPath(session);
            p.getParent().mkdirs();
            p.writeString(path.toAbsolute().normalize().toString());
        } catch (Exception e) {
            //
        }
    }

    public static void main(String[] args) {
        NSession session = Nuts.openWorkspace();
        NPath nPath = loadPath(session);
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (nPath != null) {
            f.setCurrentDirectory(nPath.toFile().get());
        }
        int r = f.showOpenDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            File sf = f.getSelectedFile();
            if (sf != null) {
                NPath path = NPath.of(sf, session);
                savePath(path, session);
                HEngine engine = new HEngineImpl(session);
                HDebugFrame debugFrame = new HDebugFrame(engine);
                HDocumentScreenRenderer renderer = engine.newScreenRenderer();
                renderer.setMessages(debugFrame.messages());
                renderer.addRendererListener(debugFrame.rendererListener());
                renderer.renderSupplier(() -> engine.loadDocument(path, debugFrame.messages()).get());
                debugFrame.run();
            }
        }
    }
}
