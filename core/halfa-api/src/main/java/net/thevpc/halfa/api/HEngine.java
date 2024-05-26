/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api;

import java.io.InputStream;
import java.util.List;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.nodes.HNodeTypeFactory;
import net.thevpc.halfa.spi.renderer.HDocumentRenderer;
import net.thevpc.halfa.spi.renderer.HDocumentScreenRenderer;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRenderer;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

/**
 * @author vpc
 */
public interface HEngine {

    List<HNodeTypeFactory> nodeTypeFactories();
    NOptional<HNodeTypeFactory> nodeTypeFactory(String id);

    HDocumentFactory documentFactory();

    NOptional<HItem> newNode(TsonElement element, HNodeFactoryParseContext ctx);

    HDocumentStreamRenderer newStreamRenderer(String type);

    HDocumentStreamRenderer newPdfRenderer();

    HDocumentScreenRenderer newScreenRenderer();

    HDocumentRenderer newRenderer(String type);

    boolean validateNode(HNode node);

    NOptional<HDocument> loadDocument(NPath of);

    NOptional<HItem> loadNode(HNode into, NPath of);

    NOptional<HDocument> loadDocument(InputStream is);

    TsonElement toTson(HDocument doc);
}
