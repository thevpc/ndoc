/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api;

import java.awt.*;
import java.io.InputStream;
import java.util.List;
import java.util.function.Function;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.document.HDocumentLoadingResult;
import net.thevpc.halfa.api.model.node.HItem;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.HNodeParser;
import net.thevpc.halfa.spi.renderer.*;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

/**
 * @author vpc
 */
public interface HEngine {

    List<HNodeParser> nodeTypeFactories();

    NOptional<HNodeParser> nodeTypeFactory(String id);

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

    TsonElement toTson(HNode node);

    NOptional<HProp> computeProperty(HNode node, String ... propertyNames);

    List<HProp> computeInheritedProperties(HNode node);

    List<HProp> computeProperties(HNode node);

    <T> NOptional<T> computePropertyValue(HNode node, String... propertyNames);

    HResource computeSource(HNode node);

    HNodeRendererManager renderManager();

    HGraphics createGraphics(Graphics2D g2d);

    void createProject(NPath path, NPath projectUrl, Function<String, String> vars);
}
