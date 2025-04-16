/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.api.style;

import net.thevpc.ndoc.api.model.elem2d.Double2;
import net.thevpc.ndoc.api.model.elem2d.HPoint2D;
import net.thevpc.ndoc.api.model.elem3d.HPoint3D;
import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElements;
import net.thevpc.nuts.elem.NToElement;
import net.thevpc.nuts.util.NNameFormat;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author vpc
 */
public class HProp implements HItem, NToElement {

    private String name;
    private NElement value;

    public static HProp ofDouble(String name, Double value) {
        return new HProp(name, NElements.of().ofDouble(value));
    }

    public static HProp ofInt(String name, Integer value) {
        return new HProp(name, NElements.of().ofInt(value));
    }

    public static HProp ofBoolean(String name, Boolean value) {
        return new HProp(name, NElements.of().ofBoolean(value));
    }

    public static HProp ofString(String name, String value) {
        return new HProp(name, NElements.of().ofString(value));
    }

    public static HProp ofObject(String name, NElement value) {
        return new HProp(name, value);
    }

    public static HProp ofStringArray(String name, String[] value) {
        return new HProp(name, NElements.of().ofStringArray(value));
    }

    public static HProp ofDouble2(String name, double x, double y) {
        return new HProp(name, NElements.of().ofUplet(NElements.of().ofDouble(x), NElements.of().ofDouble(y)));
    }

    public static HProp ofHPoint2D(String name, double x, double y) {
        return new HProp(name, NElements.of().ofUplet(NElements.of().ofDouble(x), NElements.of().ofDouble(y)));
    }

    public static HProp ofDouble2(String name, Double2 d) {
        return new HProp(name, d == null ? null : d.toElement());
    }

    public static HProp ofHPoint2D(String name, HPoint2D d) {
        return new HProp(name, d == null ? null : d.toElement());
    }

    public static HProp ofHPoint3D(String name, HPoint3D d) {
        return new HProp(name, d == null ? null : d.toElement());
    }

    public static HProp ofDouble2Array(String name, Double2... d) {
        return new HProp(name, NElements.of().ofArray(Arrays.stream(d).map(Double2::toElement).toArray(NElement[]::new)));
    }

    public static HProp ofDoubleArray(String name, double[] d) {
        return new HProp(name, NElements.of().ofDoubleArray(d));
    }

    public static HProp ofHPoint2DArray(String name, HPoint2D... d) {
        return new HProp(name, NElements.of().ofArray(Arrays.stream(d).map(HPoint2D::toElement).toArray(NElement[]::new)));
    }

    public static HProp ofHPoint3DArray(String name, HPoint3D... d) {
        return new HProp(name, NElements.of().ofArray(Arrays.stream(d).map(HPoint3D::toElement).toArray(NElement[]::new)));
    }

    public HProp(String name, NElement value) {
        this.name = name;
        this.value = value;
    }

    public HProp(String name, NToElement value) {
        this.name = name;
        this.value = value == null ? null : value.toElement();
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
        final HProp other = (HProp) obj;
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
        return NElements.of().ofPair(net.thevpc.ndoc.api.util.HUtils.toElement(name), HUtils.toElement(value));
    }
}
