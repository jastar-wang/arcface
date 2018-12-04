package com.arcsoft.face.bean;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

public class AgeInfo extends Structure {

	public PointerByReference ageArray;
	public int num;
	private int[] ages;

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("ageArray", "num");
	}

	public int[] getAges() {
		if (ages == null) {
			ages = new int[num];
			Pointer pointer = ageArray.getPointer();
			for (int i = 0; i < ages.length; i++) {
				ages[i] = pointer.getInt(4 * i);// 整形，4字节
			}
		}
		return ages;
	}

}
