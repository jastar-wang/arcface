package com.arcsoft.face.bean;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import lombok.ToString;

/**
 * 人脸特征值
 * 
 * @author Jastar·Wang
 * @email jastar_wang@163.com
 * @date 2018-12-05
 * @since 2.0
 */
@ToString
public class FaceFeature extends Structure {

	public Pointer feature;

	// 特征值大小，据官方介绍固定为1023byte
	public int featureSize;

	// 特征值数据
	private byte[] featureData;

	public FaceFeature() {
	}

	public FaceFeature(byte[] data) {
		this.featureSize = data.length;
		Pointer pointer = new Memory(data.length);
		pointer.write(0, data, 0, data.length);
		this.feature = pointer;
	}

	public String dump() {
		return feature.dump(0, featureSize);
	}

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("feature", "featureSize");
	}

	public FaceFeature deepCopy() {
		FaceFeature faceFeature = new FaceFeature(getFeatureData());
		return faceFeature;
	}

	public byte[] getFeatureData() {
		if (featureData == null) {
			featureData = feature.getByteArray(0, featureSize);
		}
		return featureData;
	}

}
