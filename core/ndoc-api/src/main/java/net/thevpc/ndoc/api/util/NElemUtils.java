/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.api.util;

import java.awt.Color;
import java.util.Arrays;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElements;
import net.thevpc.nuts.elem.NToElement;

/**
 * @author vpc
 */
public class NElemUtils {

    public static NElement toElement(NToElement[] r) {
        if(r==null){
            return NElements.of().ofNull();
        }
        return NElements.of().ofArray(Arrays.stream(r).map(x->x==null?NElements.of().ofNull():x.toElement()).toArray(NElement[]::new));
    }

    public static NElement toElement(Object r) {
        if (r == null) {
            return NElements.of().ofNull();
        }
        if (r instanceof NElement) {
            return ((NElement) r);
        }
        if (r instanceof NToElement) {
            return ((NToElement) r).toElement();
        }
        if (r.getClass().isEnum()) {
            return NElements.of().ofString(((Enum) r).name());
        }
        switch (r.getClass().getName()) {
            case "int":
            case "java.lang.Integer":
                return NElements.of().ofInt((Integer) r);
            case "boolean":
            case "java.lang.Boolean":
                return NElements.of().ofBoolean((Boolean) r);
            case "double":
            case "java.lang.Double":
                return NElements.of().ofDouble((Double) r);
            case "java.lang.String":
                return NElements.of().ofString((String) r);
            case "java.awt.Color":
                return toElement((Color) r);
            case "[D":
                return NElements.of().ofDoubleArray((double[]) r);
        }
        throw new IllegalArgumentException("not supported type to element yet..." + r.getClass());
    }

    public static NElement toElement(Color color) {
        final String PATTERN = "#%02x%02x%02x";
        return NElements.of().ofString(
                String.format(PATTERN, color.getRed(), color.getGreen(), color.getBlue()).toUpperCase()
        );
    }
}
