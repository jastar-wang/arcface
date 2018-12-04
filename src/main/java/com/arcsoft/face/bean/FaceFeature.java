package com.arcsoft.face.bean;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

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


    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected List getFieldOrder() {
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
