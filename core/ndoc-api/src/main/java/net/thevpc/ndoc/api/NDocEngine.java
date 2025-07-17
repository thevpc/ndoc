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
import net.thevpc.ndoc.api.document.NDocLogger;
import net.thevpc.ndoc.api.document.NDocDocumentLoadingResult;
import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.spi.NDocNodeParser;
import net.thevpc.ndoc.spi.renderer.*;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

/**
 * @author vpc
 */
public interface NDocEngine {

    List<NDocNodeParser> nodeTypeFactories();

    NOptional<NDocNodeParser> nodeTypeFactory(String id);

    NDocDocumentFactory documentFactory();

    NOptional<HItem> newNode(NElement element, NDocNodeFactoryParseContext ctx);

    NOptional<NDocDocumentStreamRenderer> newStreamRenderer(String type);

    NOptional<NDocDocumentStreamRenderer> newPdfRenderer();

    NOptional<NDocDocumentStreamRenderer> newHtmlRenderer();

    NOptional<NDocDocumentScreenRenderer> newScreenRenderer();

    NOptional<NDocDocumentRenderer> newRenderer(String type);

    NDocDocumentLoadingResult compileDocument(NDocument document, NDocLogger messages);

    boolean validateNode(NDocNode node);

    NDocDocumentLoadingResult loadDocument(NPath of, NDocLogger messages);

    NOptional<HItem> loadNode(NDocNode into, NPath of, NDocument document, NDocLogger messages);

    NDocDocumentLoadingResult loadDocument(InputStream is, NDocLogger messages);

    NElement toElement(NDocument doc);

    NElement toElement(NDocNode node);

    NOptional<HProp> computeProperty(NDocNode node, String... propertyNames);

    List<HProp> computeInheritedProperties(NDocNode node);

    List<HProp> computeProperties(NDocNode node);

    <T> NOptional<T> computePropertyValue(NDocNode node, String... propertyNames);

    NDocResource computeSource(NDocNode node);

    NDocNodeRendererManager renderManager();

    NDocGraphics createGraphics(Graphics2D g2d);

    void createProject(NPath path, NPath projectUrl, Function<String, String> vars);

    String[] getDefaultTemplateUrls();

    String getDefaultTemplateUrl();

    NDocNode[] compileNodeBeforeRendering(NDocNode p, NDocLogger messages);
}
