package com.arcsoft.face.bean;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

import java.util.Arrays;
import java.util.List;

public class AgeInfo extends Structure {

    public PointerByReference ageArray;
    public int num;

    private int[] ages;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected List getFieldOrder() {
        return Arrays.asList("ageArray", "num");
    }

    public int[] getAges() {
        if (ages == null) {
            ages = new int[num];
            Pointer pointer = ageArray.getPointer();
            for (int i = 0; i < ages.length; i++) {
                ages[i] = pointer.getInt(4 * i);//整形，4字节
            }
        }
        return ages;
    }

}
