package com.arcsoft.face.bean;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

import java.util.Arrays;
import java.util.List;

public class GenderInfo extends Structure {

    public PointerByReference genderArray;
    public int num;

    private int[] genders; //0 男性 1 女性

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("genderArray", "num");
    }

    public int[] getGenders() {
        if (genders == null) {
            genders = new int[num];
            Pointer pointer = genderArray.getPointer();
            for (int i = 0; i < genders.length; i++) {
                genders[i] = pointer.getInt(4 * i);//整形，4字节
            }
        }
        return genders;
    }

}
