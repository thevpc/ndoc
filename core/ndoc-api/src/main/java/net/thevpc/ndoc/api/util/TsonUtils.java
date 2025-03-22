/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.api.util;

import java.awt.Color;
import java.util.Arrays;

import net.thevpc.tson.ToTson;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonElementBase;

/**
 * @author vpc
 */
public class TsonUtils {

    public static TsonElement toTson(ToTson[] r) {
        if(r==null){
            return Tson.ofNull();
        }
        return Tson.of(Tson.ofArray(Arrays.stream(r).map(x->x==null?Tson.ofNull():x.toTson()).toArray(TsonElement[]::new)).build());
    }

    public static TsonElement toTson(Object r) {
        if (r == null) {
            return Tson.ofNull();
        }
        if (r instanceof TsonElementBase) {
            return ((TsonElementBase) r).build();
        }
        if (r instanceof ToTson) {
            return ((ToTson) r).toTson();
        }
        if (r.getClass().isEnum()) {
            return Tson.ofString(((Enum) r).name());
        }
        switch (r.getClass().getName()) {
            case "int":
            case "java.lang.Integer":
                return Tson.ofInt((Integer) r);
            case "boolean":
            case "java.lang.Boolean":
                return Tson.ofBoolean((Boolean) r);
            case "double":
            case "java.lang.Double":
                return Tson.ofDouble((Double) r);
            case "java.lang.String":
                return Tson.ofString((String) r);
            case "java.awt.Color":
                return toTson((Color) r);
            case "[D":
                return Tson.ofArray(Arrays.stream((double[]) r).mapToObj(x -> Tson.ofDouble(x)).toArray(TsonElementBase[]::new)).build();
        }
        throw new IllegalArgumentException("not supported type to tson yet..." + r.getClass());
    }

    public static TsonElement toTson(Color color) {
        final String PATTERN = "#%02x%02x%02x";
        return Tson.ofString(
                String.format(PATTERN, color.getRed(), color.getGreen(), color.getBlue()).toUpperCase()
        );
    }
}
