/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api;

import java.io.InputStream;
import net.thevpc.halfa.api.model.HDocument;
import net.thevpc.halfa.api.model.HDocumentPart;
import net.thevpc.halfa.api.model.HPage;
import net.thevpc.halfa.api.model.HPagePart;
import net.thevpc.halfa.api.model.HText;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRenderer;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

/**
 *
 * @author vpc
 */
public interface HalfaEngine {

    HDocument newDocument();

    HPage newPage();

    NOptional<HDocumentPart> newDocumentPart(TsonElement element);

    NOptional<HPagePart> newPagePart(TsonElement element);

    HDocumentStreamRenderer newStreamRenderer(String type);

    public HText newText();

    public HText newText(String hello);

    public HDocument loadDocument(NPath of);

    public HDocument loadDocument(InputStream is);
}
