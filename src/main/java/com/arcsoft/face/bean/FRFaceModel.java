package com.arcsoft.face.bean;

import com.arcsoft.face.api.CLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;

import java.util.Arrays;
import java.util.List;

public class FRFaceModel extends Structure {

    public ByteByReference pbFeature;
    public int lFeatureSize;

    protected boolean bAllocByMalloc;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[]{"pbFeature", "lFeatureSize"});
    }

    public FRFaceModel deepCopy() throws Exception {

        if (!isValid()) {
            throw new Exception("invalid feature");
        }

        FRFaceModel feature = new FRFaceModel();
        feature.bAllocByMalloc = true;
        feature.lFeatureSize = lFeatureSize;
        feature.pbFeature = new ByteByReference();
        feature.pbFeature.setPointer(CLibrary.INSTANCE.malloc(feature.lFeatureSize));
        CLibrary.INSTANCE.memcpy(feature.pbFeature.getPointer(), pbFeature.getPointer(), feature.lFeatureSize);
        return feature;
    }

    public synchronized void freeUnmanaged() {
        if (bAllocByMalloc && isValid()) {
            CLibrary.INSTANCE.free(pbFeature.getPointer());
            pbFeature = null;
            // System.out.println("gc feature freeUnmanaged");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        freeUnmanaged();
    }

    public static FRFaceModel fromByteArray(byte[] byteArray) throws Exception {
        if (byteArray == null) {
            throw new Exception("invalid byteArray");
        }

        FRFaceModel feature = new FRFaceModel();
        feature.lFeatureSize = byteArray.length;
        feature.bAllocByMalloc = true;
        feature.pbFeature = new ByteByReference();
        feature.pbFeature.setPointer(CLibrary.INSTANCE.malloc(feature.lFeatureSize));
        feature.pbFeature.getPointer().write(0, byteArray, 0, feature.lFeatureSize);
        return feature;
    }

    public byte[] toByteArray() throws Exception {
        if (!isValid()) {
            throw new Exception("invalid feature");
        }
        return pbFeature.getPointer().getByteArray(0, lFeatureSize);
    }

    private boolean isValid() {
        return ((pbFeature != null) && (Pointer.nativeValue(pbFeature.getPointer()) != 0));
    }
}
