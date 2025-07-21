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
import net.thevpc.ndoc.api.model.fct.NDocFunction;
import net.thevpc.ndoc.api.model.fct.NDocFunctionArg;
import net.thevpc.ndoc.api.model.node.NDocItem;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.ndoc.api.style.NDocProp;
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

    NDocLogger messages();
    NDocLogger messages(NDocLogger messages);
    List<NDocNodeParser> nodeTypeFactories();

    NOptional<NDocFunction> findFunction(NDocItem node, String name, NDocFunctionArg... args);

    NOptional<NDocNodeParser> nodeTypeParser(String id);

    NDocDocumentFactory documentFactory();

    NOptional<NDocItem> newNode(NElement element, NDocNodeFactoryParseContext ctx);

    NOptional<NDocDocumentStreamRenderer> newStreamRenderer(String type);

    NOptional<NDocDocumentStreamRenderer> newPdfRenderer();

    NOptional<NDocDocumentStreamRenderer> newHtmlRenderer();

    NOptional<NDocDocumentScreenRenderer> newScreenRenderer();

    NOptional<NDocDocumentRenderer> newRenderer(String type);

    NDocDocumentLoadingResult compileDocument(NDocument document);

    List<NDocNode> compileNode(NDocNode node, NDocument document);

    List<NDocNode> compileNode(NDocNode node, CompilePageContext context);
    List<NDocNode> compileItem(NDocItem node, CompilePageContext context);

    boolean validateNode(NDocNode node);

    NDocDocumentLoadingResult loadDocument(NPath of);

    NOptional<NDocItem> loadNode(NDocNode into, NPath of, NDocument document);

    NDocDocumentLoadingResult loadDocument(InputStream is);

    NElement toElement(NDocument doc);

    NElement toElement(NDocNode node);

    NOptional<NDocProp> computeProperty(NDocNode node, String... propertyNames);

    List<NDocProp> computeInheritedProperties(NDocNode node);

    List<NDocProp> computeProperties(NDocNode node);

    <T> NOptional<T> computePropertyValue(NDocNode node, String... propertyNames);

    NDocResource computeSource(NDocItem node);

    NDocNodeRendererManager renderManager();

    NDocGraphics createGraphics(Graphics2D g2d);

    void createProject(NPath path, NPath projectUrl, Function<String, String> vars);

    String[] getDefaultTemplateUrls();

    String getDefaultTemplateUrl();

    NDocFunctionArg createRawArg(NDocNode node, NElement expression) ;

    NElement evalExpression(NDocNode node, NElement expression);
    NElement resolveVarValue(NDocNode node, String varName) ;
}
