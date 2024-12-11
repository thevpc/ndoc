package net.thevpc.halfa.test;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HDocumentLoadingResult;
import net.thevpc.halfa.api.document.HMessageListImpl;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.nuts.Nuts;

import java.io.ByteArrayInputStream;

public class TestEngine {
    public static void main(String[] args) {
        Nuts.openWorkspace().setSharedInstance();
        HEngine e=new HEngineImpl();
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
        HMessageListImpl messages = new HMessageListImpl(null);
        HDocumentLoadingResult d = e.loadDocument(
                new ByteArrayInputStream(document.getBytes()), messages
        );
        HDocument dd = d.get();
        HDocumentLoadingResult c = e.compileDocument(dd, messages);
    }
}
