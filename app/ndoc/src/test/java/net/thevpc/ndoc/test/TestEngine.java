package net.thevpc.ndoc.test;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.NDocDocumentLoadingResult;
import net.thevpc.ndoc.engine.DefaultNDocEngine;
import net.thevpc.nuts.Nuts;

import java.io.ByteArrayInputStream;

public class TestEngine {
    public static void main(String[] args) {
        Nuts.openWorkspace().share();
        NDocEngine e=new DefaultNDocEngine();
        String document="\n" +
                "$a=1\n" +
                "if($a==1){\n" +
                " rectangle()\n" +
//                " } elseif($a==2) {\n" +
//                "  triangle()\n" +
                " } else {\n" +
                "  circle()\n" +
                "}\n"
                ;
        System.out.println(document);
        NDocDocumentLoadingResult d = e.loadDocument(
                new ByteArrayInputStream(document.getBytes())
        );
        NDocument dd = d.get();
        NDocDocumentLoadingResult c = e.compileDocument(dd);
    }
}
