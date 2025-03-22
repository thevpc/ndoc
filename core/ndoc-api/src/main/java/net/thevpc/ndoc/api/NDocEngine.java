/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.api;

import java.awt.*;
import java.io.InputStream;
import java.util.List;
import java.util.function.Function;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.HLogger;
import net.thevpc.ndoc.api.document.HDocumentLoadingResult;
import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.resources.HResource;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.spi.NDocNodeParser;
import net.thevpc.ndoc.spi.renderer.*;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

/**
 * @author vpc
 */
public interface NDocEngine {

    List<NDocNodeParser> nodeTypeFactories();

    NOptional<NDocNodeParser> nodeTypeFactory(String id);

    NDocDocumentFactory documentFactory();

    NOptional<HItem> newNode(TsonElement element, NDocNodeFactoryParseContext ctx);

    NOptional<NDocDocumentStreamRenderer> newStreamRenderer(String type);

    NOptional<NDocDocumentStreamRenderer> newPdfRenderer();
    NOptional<NDocDocumentStreamRenderer> newHtmlRenderer();

    NOptional<NDocDocumentScreenRenderer> newScreenRenderer();

    NOptional<NDocDocumentRenderer> newRenderer(String type);

    HDocumentLoadingResult compileDocument(NDocument document, HLogger messages);

    boolean validateNode(HNode node);

    HDocumentLoadingResult loadDocument(NPath of, HLogger messages);

    NOptional<HItem> loadNode(HNode into, NPath of, NDocument document, HLogger messages);

    HDocumentLoadingResult loadDocument(InputStream is, HLogger messages);

    TsonElement toTson(NDocument doc);

    TsonElement toTson(HNode node);

    NOptional<HProp> computeProperty(HNode node, String ... propertyNames);

    List<HProp> computeInheritedProperties(HNode node);

    List<HProp> computeProperties(HNode node);

    <T> NOptional<T> computePropertyValue(HNode node, String... propertyNames);

    HResource computeSource(HNode node);

    NDocNodeRendererManager renderManager();

    NDocGraphics createGraphics(Graphics2D g2d);

    void createProject(NPath path, NPath projectUrl, Function<String, String> vars);

    String[] getDefaultTemplateUrls();
    
    String getDefaultTemplateUrl();
}
