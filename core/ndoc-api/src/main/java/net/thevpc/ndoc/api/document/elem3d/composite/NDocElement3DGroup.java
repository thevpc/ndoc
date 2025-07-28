package net.thevpc.ndoc.api.document.elem3d.composite;

import net.thevpc.ndoc.api.document.elem3d.AbstractElement3D;
import net.thevpc.ndoc.api.document.elem3d.NDocElement3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NDocElement3DGroup extends AbstractElement3D {
    private List<NDocElement3D> elements = new ArrayList<>();

    public NDocElement3DGroup(NDocElement3D... elements) {
        this.elements = new ArrayList<>(Arrays.asList(elements));
    }

    public NDocElement3DGroup add(NDocElement3D e) {
        if (e != null) {
            elements.add(e);
        }
        return this;
    }

    public List<NDocElement3D> getElements() {
        return elements;
    }


}
