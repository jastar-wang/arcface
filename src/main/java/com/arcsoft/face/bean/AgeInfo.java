package com.arcsoft.face.bean;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

import lombok.ToString;

/**
 * 年龄信息
 * 
 * @author Jastar·Wang
 * @date 2018-12-05
 * @since 2.0
 */
@ToString
public class AgeInfo extends Structure {

	public PointerByReference ageArray;

	// 检测到的人脸个数
	public int num;

	// 对应的年龄，"0" 代表不确定，大于0的数值代表检测出来的年龄结果
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
