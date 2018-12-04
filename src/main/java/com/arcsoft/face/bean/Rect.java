package com.arcsoft.face.bean;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class Rect extends Structure {
    public static class ByValue extends Rect implements Structure.ByValue {
        public ByValue() {

        }

        public ByValue(Pointer p) {
            super(p);
        }
    }

    public static class ByReference extends Rect implements Structure.ByReference {
        public ByReference() {

        }

        public ByReference(Pointer p) {
            super(p);
        }
    }

    public int left;
    public int top;
    public int right;
    public int bottom;

    public Rect() {

    }

    public Rect(Pointer p) {
        super(p);
        read();
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[]{"left", "top", "right", "bottom"});
    }
}