/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine;

import net.thevpc.halfa.api.HalfaEngine;
import net.thevpc.halfa.api.model.HDocument;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.tson.TsonElement;

import static net.thevpc.tson.TsonElementType.ARRAY;
import static net.thevpc.tson.TsonElementType.FUNCTION;
import static net.thevpc.tson.TsonElementType.OBJECT;

import net.thevpc.tson.TsonFunction;

/**
 * @author vpc
 */
public class HTsonReader {

    private HalfaEngine engine;
    private NSession session;

    public HTsonReader(HalfaEngine engine, NSession session) {
        this.engine = engine;
        this.session = session;
    }

    public HDocument convertToHDocument(TsonElement c) {
        switch (c.getType()) {
            case OBJECT: {
                HDocument doc = engine.factory().document();
                for (TsonElement e : c.toObject().getAll()) {
                    doc.addDocumentPart(engine.newDocumentPart(e).get());
                }
                return doc;
            }
            case ARRAY: {
                HDocument doc = engine.factory().document();
                for (TsonElement e : c.toArray().getAll()) {
                    doc.addDocumentPart(engine.newDocumentPart(e).get());
                }
                return doc;
            }
            case FUNCTION: {
                HDocument doc = engine.factory().document();
                TsonFunction ff = c.toFunction();
                for (TsonElement e : ff.getAll()) {
                    doc.addDocumentPart(engine.newDocumentPart(e).get());
                }
                return doc;
            }
        }
        throw new NIllegalArgumentException(session, NMsg.ofC("unable to resolve as Document : %s", c));
    }

}
