package com.arcsoft.face.bean;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

public class GenderInfo extends Structure {

	// 男性
	public static final int MEN = 0;
	// 女性
	public static final int WOMEN = 1;

	public PointerByReference genderArray;
	public int num;

	private int[] genders;

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("genderArray", "num");
	}

	public int[] getGenders() {
		if (genders == null) {
			genders = new int[num];
			Pointer pointer = genderArray.getPointer();
			for (int i = 0; i < genders.length; i++) {
				genders[i] = pointer.getInt(4 * i);// 整形，4字节
			}
		}
		return genders;
	}

}
