/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api;

import java.io.InputStream;

import net.thevpc.halfa.HalfaDocumentFactory;
import net.thevpc.halfa.api.model.*;
import net.thevpc.halfa.spi.renderer.HDocumentRenderer;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRenderer;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

/**
 * @author vpc
 */
public interface HalfaEngine {

    HalfaDocumentFactory factory();

    NOptional<HDocumentPart> newDocumentPart(TsonElement element);

    NOptional<HPagePart> newPagePart(TsonElement element);

    HDocumentStreamRenderer newStreamRenderer(String type);

    HDocumentRenderer newRenderer(String type);


    HDocument loadDocument(NPath of);

    HDocument loadDocument(InputStream is);
}
