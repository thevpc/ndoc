package net.thevpc.ndoc.api.model.elem3d.composite;

import net.thevpc.ndoc.api.model.elem3d.AbstractElement3D;
import net.thevpc.ndoc.api.model.elem3d.HElement3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Element3DGroup extends AbstractElement3D {
    private List<HElement3D> elements = new ArrayList<>();

    public Element3DGroup(HElement3D... elements) {
        this.elements = new ArrayList<>(Arrays.asList(elements));
    }

    public Element3DGroup add(HElement3D e) {
        if (e != null) {
            elements.add(e);
        }
        return this;
    }

    public List<HElement3D> getElements() {
        return elements;
    }


}
