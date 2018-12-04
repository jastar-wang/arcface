package com.arcsoft.face.bean;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import lombok.ToString;

@ToString
public class FaceFeature extends Structure {

	public Pointer feature;
	public int featureSize;

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
