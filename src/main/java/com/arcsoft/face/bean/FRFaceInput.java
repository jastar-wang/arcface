package com.arcsoft.face.bean;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class FRFaceInput extends Structure {

    public Rect.ByValue rcFace;
    public int lOrient;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[]{"rcFace", "lOrient"});
    }
}
