package com.arcsoft.face.bean;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

import lombok.ToString;

/**
 * 性别
 * 
 * @author Jastar·Wang
 * @date 2018-12-05
 * @since 2.0
 */
@ToString
public class GenderInfo extends Structure {

	// 男性
	public static final int MEN = 0;
	// 女性
	public static final int WOMEN = 1;
	// 不确定
	public static final int UNCERTAIN = -1;

	public PointerByReference genderArray;

	// 检测到的人脸个数
	public int num;

	// 性别信息，"0" 表示 男性, "1" 表示 女性, "-1" 表示不确定
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
