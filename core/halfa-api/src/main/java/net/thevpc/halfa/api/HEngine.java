/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api;

import java.io.InputStream;
import java.util.List;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.document.HDocumentLoadingResult;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.halfa.api.style.HProp;
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

    HDocumentLoadingResult compileDocument(HDocument document, HMessageList messages);

    boolean validateNode(HNode node);

    HDocumentLoadingResult loadDocument(NPath of, HMessageList messages);

    NOptional<HItem> loadNode(HNode into, NPath of, HDocument document, HMessageList messages);

    HDocumentLoadingResult loadDocument(InputStream is, HMessageList messages);

    TsonElement toTson(HDocument doc);

    NOptional<HProp> computeProperty(HNode node, String propertyName);

    List<HProp> computeInheritedProperties(HNode node);

    List<HProp> computeProperties(HNode node);

    <T> NOptional<T> computePropertyValue(HNode node, String propertyName);

    HResource computeSource(HNode node);
}
