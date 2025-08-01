/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.api.engine;

import java.awt.*;
import java.io.InputStream;
import java.util.List;
import java.util.function.Function;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.NDocDocumentLoadingResult;
import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.eval.NDocCompilePageContext;
import net.thevpc.ndoc.api.eval.NDocFunction;
import net.thevpc.ndoc.api.eval.NDocFunctionArg;
import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.eval.NDocVar;
import net.thevpc.ndoc.api.log.NDocLogger;
import net.thevpc.ndoc.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.api.parser.NDocNodeParser;
import net.thevpc.ndoc.api.renderer.*;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererFlavor;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

/**
 * @author vpc
 */
public interface NDocEngine {

    NDocLogger log();
    NDocEngineTools tools();

    NDocLogger addLog(NDocLogger messages);

    NDocLogger removeLog(NDocLogger messages);

    List<NDocNodeParser> nodeTypeFactories();

    NOptional<NDocFunction> findFunction(NDocItem node, String name, NDocFunctionArg... args);

    NDocNode newDefaultNode(String id);

    NOptional<NDocNodeParser> nodeTypeParser(String id);

    NDocDocumentFactory documentFactory();

    NOptional<NDocItem> newNode(NElement element, NDocNodeFactoryParseContext ctx);

    NOptional<NDocDocumentStreamRenderer> newStreamRenderer(String type);

    NOptional<NDocDocumentStreamRenderer> newPdfRenderer();

    NOptional<NDocDocumentStreamRenderer> newHtmlRenderer();

    NOptional<NDocDocumentScreenRenderer> newScreenRenderer();

    NOptional<NDocDocumentRenderer> newRenderer(String type);

    NDocDocumentLoadingResult compileDocument(NDocument document);

    List<NDocNode> compilePageNode(NDocNode node, NDocument document);

    List<NDocNode> compilePageNode(NDocNode node, NDocCompilePageContext context);

    List<NDocNode> compileItem(NDocItem node, NDocCompilePageContext context);

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

    NDocNodeRendererManager renderManager();

    NDocGraphics createGraphics(Graphics2D g2d);

    void createProject(NPath path, NPath projectUrl, Function<String, String> vars);

    String[] getDefaultTemplateUrls();

    String getDefaultTemplateUrl();

    NDocFunctionArg createRawArg(NDocNode node, NElement expression);

    NElement evalExpression(NElement expression, NDocNode node);

    NElement resolveVarValue(String varName, NDocNode node);

    NOptional<NDocVar> findVar(String varName, NDocNode node);

    NPath resolvePath(NElement path, NDocNode node);

    NOptional<NDocTextRendererFlavor> textRendererFlavor(String id);

    List<NDocTextRendererFlavor> textRendererFlavors();
}
