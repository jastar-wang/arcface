package com.arcsoft.face.bean;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

import java.util.Arrays;
import java.util.List;

public class FDFaceRes extends Structure {
    public static class ByReference extends FDFaceRes implements Structure.ByReference {
        public ByReference() {

        }

        public ByReference(Pointer p) {
            super(p);
        }
    }

    ;

    public int nFace;
    public Rect.ByReference rcFace;
    public IntByReference lfaceOrient;

    public FDFaceRes() {
    }

    public FDFaceRes(Pointer p) {
        super(p);
        read();
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[]{"nFace", "rcFace", "lfaceOrient"});
    }
}
