/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api;

import java.io.InputStream;
import java.util.Set;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
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

    HDocumentFactory documentFactory();


    NOptional<HItem> newDocumentRoot(TsonElement element);

    NOptional<HItem> newNode(TsonElement element, HNode currentNode, Set<HNodeType> expected, HNodeFactoryParseContext ctx, Object source);

    NOptional<HItem> newPageChild(TsonElement element, HNode currentNode, HNodeFactoryParseContext ctx);

    NOptional<HItem> newDocumentChild(TsonElement element, HNode currentNode, HNodeFactoryParseContext ctx);

    HDocumentStreamRenderer newStreamRenderer(String type);

    HDocumentStreamRenderer newPdfRenderer();

    HDocumentScreenRenderer newScreenRenderer();

    HDocumentRenderer newRenderer(String type);


    NOptional<HDocument> loadDocument(NPath of);

    NOptional<HItem> loadNode(HNode into,Set<HNodeType> expected, NPath of);

    NOptional<HDocument> loadDocument(InputStream is);
}
