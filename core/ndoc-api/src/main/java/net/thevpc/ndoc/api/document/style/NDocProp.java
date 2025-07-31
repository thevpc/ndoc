/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.api.document.style;

import net.thevpc.ndoc.api.document.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.document.elem2d.NDocPoint2D;
import net.thevpc.ndoc.api.document.elem3d.NDocPoint3D;
import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NToElement;
import net.thevpc.nuts.util.NNameFormat;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author vpc
 */
public class NDocProp implements NDocItem, NToElement {

    private String name;
    private NElement value;
    private NDocItem parent;

    public static NDocProp ofDouble(String name, Double value) {
        return of(name, NElement.ofDouble(value));
    }

    public static NDocProp ofInt(String name, Integer value) {
        return of(name, NElement.ofInt(value));
    }

    public static NDocProp ofBoolean(String name, Boolean value) {
        return of(name, NElement.ofBoolean(value));
    }

    public static NDocProp ofString(String name, String value) {
        return of(name, NElement.ofString(value));
    }

    public static NDocProp ofObject(String name, NElement value) {
        return of(name, value);
    }

    public static NDocProp ofStringArray(String name, String[] value) {
        return of(name, NElement.ofStringArray(value));
    }

    public static NDocProp ofDouble2(String name, double x, double y) {
        return of(name, NElement.ofUplet(NElement.ofDouble(x), NElement.ofDouble(y)));
    }

    public static NDocProp ofHPoint2D(String name, double x, double y) {
        return of(name, NElement.ofUplet(NElement.ofDouble(x), NElement.ofDouble(y)));
    }

    public static NDocProp ofDouble2(String name, NDocDouble2 d) {
        return of(name, d == null ? null : d.toElement());
    }

    public static NDocProp ofHPoint2D(String name, NDocPoint2D d) {
        return of(name, d == null ? null : d.toElement());
    }

    public static NDocProp ofHPoint3D(String name, NDocPoint3D d) {
        return of(name, d == null ? null : d.toElement());
    }

    public static NDocProp ofDouble2Array(String name, NDocDouble2... d) {
        return of(name, NElement.ofArray(Arrays.stream(d).map(NDocDouble2::toElement).toArray(NElement[]::new)));
    }

    public static NDocProp ofDoubleArray(String name, double[] d) {
        return of(name, NElement.ofDoubleArray(d));
    }

    public static NDocProp ofIntArray(String name, int[] d) {
        return of(name, NElement.ofIntArray(d));
    }

    public static NDocProp ofHPoint2DArray(String name, NDocPoint2D... d) {
        return of(name, NElement.ofArray(Arrays.stream(d).map(NDocPoint2D::toElement).toArray(NElement[]::new)));
    }

    public static NDocProp ofHPoint3DArray(String name, NDocPoint3D... d) {
        return of(name, NElement.ofArray(Arrays.stream(d).map(NDocPoint3D::toElement).toArray(NElement[]::new)));
    }

    public NDocProp(String name, NElement value, NDocItem parent) {
        this.name = name;
        this.value = value;
        this.parent = parent;
    }

    public NDocProp(String name, NToElement value, NDocItem parent) {
        this.name = name;
        this.value = value == null ? null : value.toElement();
        this.parent = parent;
    }

    public static NDocProp of(String name, NElement v) {
        return new NDocProp(name, v, null);
    }

    public static NDocProp of(String name, NToElement v) {
        return new NDocProp(name,(v==null?null:NDocUtils.addCompilerDeclarationPathDummy(v.toElement())), null);
    }

    @Override
    public NDocResource source() {
        return null;
    }

    @Override
    public NDocItem parent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public NElement getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NDocProp other = (NDocProp) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.value, other.value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(NNameFormat.LOWER_KEBAB_CASE.format(name));
        sb.append(":");
        Object vv = value;
        if (vv != null) {
            if (vv.getClass().isArray()) {
                Class<?> ct = vv.getClass().getComponentType();
                if (ct.isPrimitive()) {
                    switch (ct.getName()) {
                        case "double": {
                            sb.append(Arrays.toString((double[]) vv));
                            break;
                        }
                        case "int": {
                            sb.append(Arrays.toString((int[]) vv));
                            break;
                        }
                        case "long": {
                            sb.append(Arrays.toString((long[]) vv));
                            break;
                        }
                        case "boolean": {
                            sb.append(Arrays.toString((boolean[]) vv));
                            break;
                        }
                        default: {
                            sb.append(vv);
                        }
                    }
                } else {
                    sb.append(Arrays.asList((Object[]) vv));
                }
            } else {
                sb.append(vv);
            }
        } else {
            sb.append(vv);
        }
        return sb.toString();
    }

    public NElement toElement() {
        return NElement.ofPair(NDocUtils.toElement(name), NDocUtils.toElement(value));
    }
}
