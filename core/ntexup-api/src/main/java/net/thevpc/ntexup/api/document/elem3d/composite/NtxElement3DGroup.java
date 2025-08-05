package net.thevpc.ntexup.api.document.elem3d.composite;

import net.thevpc.ntexup.api.document.elem3d.AbstractNTxElement3D;
import net.thevpc.ntexup.api.document.elem3d.NtxElement3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NtxElement3DGroup extends AbstractNTxElement3D {
    private List<NtxElement3D> elements = new ArrayList<>();

    public NtxElement3DGroup(NtxElement3D... elements) {
        this.elements = new ArrayList<>(Arrays.asList(elements));
    }

    public NtxElement3DGroup add(NtxElement3D e) {
        if (e != null) {
            elements.add(e);
        }
        return this;
    }

    public List<NtxElement3D> getElements() {
        return elements;
    }


}
