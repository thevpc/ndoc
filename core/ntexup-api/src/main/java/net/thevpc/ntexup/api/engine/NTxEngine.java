/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.api.engine;

import java.awt.*;
import java.io.InputStream;
import java.util.List;
import java.util.function.Function;

import net.thevpc.ntexup.api.document.NTxDocumentFactory;
import net.thevpc.ntexup.api.document.NTxDocument;
import net.thevpc.ntexup.api.document.NTxDocumentLoadingResult;
import net.thevpc.ntexup.api.document.style.NTxProp;
import net.thevpc.ntexup.api.eval.NDocCompilePageContext;
import net.thevpc.ntexup.api.eval.NDocVarProvider;
import net.thevpc.ntexup.api.extension.NTxFunction;
import net.thevpc.ntexup.api.eval.NTxFunctionArg;
import net.thevpc.ntexup.api.document.node.NTxItem;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.eval.NDocVar;
import net.thevpc.ntexup.api.log.NDocLogger;
import net.thevpc.ntexup.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ntexup.api.parser.NDocNodeParser;
import net.thevpc.ntexup.api.renderer.*;
import net.thevpc.ntexup.api.renderer.text.NDocTextRendererFlavor;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

/**
 * @author vpc
 */
public interface NTxEngine {
    String FILE_EXT="ntx";
    String FILE_DOT_EXT=".ntx";
    NDocLogger log();
    NTxEngineTools tools();

    NDocLogger addLog(NDocLogger messages);

    NDocLogger removeLog(NDocLogger messages);

    List<NDocNodeParser> nodeTypeFactories();

    NOptional<NTxFunction> findFunction(NTxItem node, String name, NTxFunctionArg... args);

    NTxNode newDefaultNode(String id);

    NOptional<NDocNodeParser> nodeTypeParser(String id);

    NTxDocumentFactory documentFactory();

    NOptional<NTxItem> newNode(NElement element, NDocNodeFactoryParseContext ctx);

    NOptional<NTxDocumentStreamRenderer> newStreamRenderer(String type);

    NOptional<NTxDocumentStreamRenderer> newPdfRenderer();

    NOptional<NTxDocumentStreamRenderer> newHtmlRenderer();

    NOptional<NTxDocumentScreenRenderer> newScreenRenderer();

    NOptional<NTxDocumentRenderer> newRenderer(String type);

    NTxDocumentLoadingResult compileDocument(NTxDocument document);

    List<NTxNode> compilePageNode(NTxNode node, NTxDocument document);

    List<NTxNode> compilePageNode(NTxNode node, NDocCompilePageContext context);

    List<NTxNode> compileItem(NTxItem node, NDocCompilePageContext context);

    boolean validateNode(NTxNode node);

    NTxDocumentLoadingResult loadDocument(NPath of);

    NOptional<NTxItem> loadNode(NTxNode into, NPath of, NTxDocument document);

    NTxDocumentLoadingResult loadDocument(InputStream is);

    NElement toElement(NTxDocument doc);

    NElement toElement(NTxNode node);

    NOptional<NTxProp> computeProperty(NTxNode node, String... propertyNames);

    List<NTxProp> computeInheritedProperties(NTxNode node);

    List<NTxProp> computeProperties(NTxNode node);

    <T> NOptional<T> computePropertyValue(NTxNode node, String... propertyNames);

    NDocNodeRendererManager renderManager();

    NDocGraphics createGraphics(Graphics2D g2d);

    void createProject(NPath path, NPath projectUrl, Function<String, String> vars);

    NTxTemplateInfo[] getTemplates();

    NElement evalExpression(NElement expression, NTxNode node, NDocVarProvider varProvider);

    NElement resolveVarValue(String varName, NTxNode node, NDocVarProvider varProvider);

    NOptional<NDocVar> findVar(String varName, NTxNode node, NDocVarProvider varProvider);

    NPath resolvePath(NElement path, NTxNode node);

    NOptional<NDocTextRendererFlavor> textRendererFlavor(String id);

    List<NDocTextRendererFlavor> textRendererFlavors();
}
