package com.arcsoft.face;

import com.arcsoft.face.api.FRLibrary;
import com.arcsoft.face.api.CLibrary;
import com.arcsoft.face.bean.*;
import com.arcsoft.face.util.Config;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.PointerByReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FREngine {
    private static FREngine engin = new FREngine();
    private static final int FR_WORKBUF_SIZE = 40 * 1024 * 1024;
    private Pointer hFREngine;
    private Pointer pFRWorkMem;

    public static FREngine getInstance() {
        return engin;
    }

    private FREngine() {
        boolean isLinux64 = System.getProperty("os.name").equals("Linux")
                && System.getProperty("os.arch").equals("amd64");

        this.pFRWorkMem = CLibrary.INSTANCE.malloc(FR_WORKBUF_SIZE);
        PointerByReference phFREngine = new PointerByReference();
        NativeLong ret;
        if (isLinux64) {
            ret = FRLibrary.INSTANCE.AFR_FSDK_InitialEngine(Config.Linux.APPID, Config.Linux.FR_SDKKEY,
                    pFRWorkMem, FR_WORKBUF_SIZE, phFREngine);
        } else {
            ret = FRLibrary.INSTANCE.AFR_FSDK_InitialEngine(Config.Win.APPID, Config.Win.FR_SDKKEY, pFRWorkMem,
                    FR_WORKBUF_SIZE, phFREngine);
        }
        if (ret.longValue() != 0) {
            CLibrary.INSTANCE.free(this.pFRWorkMem);
            log.error(String.format("AFR_FSDK_InitialEngine ret 0x%x", ret.longValue()));
            return;
        }
        this.hFREngine = phFREngine.getValue();
    }

    public String getVersion() {
        FRVersion versionFR = FRLibrary.INSTANCE.AFR_FSDK_GetVersion(this.hFREngine);
        return versionFR.Version + '/' + versionFR.BuildDate;
    }

    public void UninitialEngine() {
        FRLibrary.INSTANCE.AFR_FSDK_UninitialEngine(this.hFREngine);
        CLibrary.INSTANCE.free(this.pFRWorkMem);
    }

    public FRFaceModel extractFRFeature(AsvlOffScreen inputImg, FaceInfo faceInfo) {
        FRFaceInput faceinput = new FRFaceInput();
        faceinput.lOrient = faceInfo.orient;
        faceinput.rcFace.left = faceInfo.left;
        faceinput.rcFace.top = faceInfo.top;
        faceinput.rcFace.right = faceInfo.right;
        faceinput.rcFace.bottom = faceInfo.bottom;

        FRFaceModel faceFeature = new FRFaceModel();
        NativeLong ret = FRLibrary.INSTANCE.AFR_FSDK_ExtractFRFeature(this.hFREngine, inputImg, faceinput,
                faceFeature);
        if (ret.longValue() != 0) {
            log.warn(String.format("AFR_FSDK_ExtractFRFeature ret 0x%x", ret.longValue()));
            return null;
        }

        try {
            return faceFeature.deepCopy();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public float compareFaceSimilarity(AsvlOffScreen inputImgA, AsvlOffScreen inputImgB) {
        // Do Face Detect
        FaceInfo[] faceInfosA = FDEngine.getInstance().doFaceDetection(inputImgA);
        if (faceInfosA.length < 1) {
            log.info("no face in Image A ");
            return 0.0f;
        }

        FaceInfo[] faceInfosB = FDEngine.getInstance().doFaceDetection(inputImgB);
        if (faceInfosB.length < 1) {
            log.info("no face in Image B ");
            return 0.0f;
        }

        // Extract Face Feature
        FRFaceModel faceFeatureA = extractFRFeature(inputImgA, faceInfosA[0]);
        if (faceFeatureA == null) {
            log.info("extract face feature in Image A failed");
            return 0.0f;
        }

        FRFaceModel faceFeatureB = extractFRFeature(inputImgB, faceInfosB[0]);
        if (faceFeatureB == null) {
            log.info("extract face feature in Image B failed");
            faceFeatureA.freeUnmanaged();
            return 0.0f;
        }

        // calc similarity between faceA and faceB
        FloatByReference fSimilScore = new FloatByReference(0.0f);
        NativeLong ret = FRLibrary.INSTANCE.AFR_FSDK_FacePairMatching(this.hFREngine, faceFeatureA, faceFeatureB,
                fSimilScore);
        faceFeatureA.freeUnmanaged();
        faceFeatureB.freeUnmanaged();
        if (ret.longValue() != 0) {
            log.info(String.format("AFR_FSDK_FacePairMatching failed:ret 0x%x", ret.longValue()));
            return 0.0f;
        }
        return fSimilScore.getValue();
    }

    public float compareFaceSimilarity(AsvlOffScreen inputImgA, FRFaceModel faceFeatureB) {
        // Do Face Detect
        FaceInfo[] faceInfosA = FDEngine.getInstance().doFaceDetection(inputImgA);
        if (faceInfosA.length < 1) {
            log.info("no face in Image inputImgA ");
            return 0.0f;
        }

        // Extract Face Feature
        FRFaceModel faceFeatureA = extractFRFeature(inputImgA, faceInfosA[0]);
        if (faceFeatureA == null) {
            log.info("extract face feature in Image A failed");
            return 0.0f;
        }

        // calc similarity between faceA and faceB
        FloatByReference fSimilScore = new FloatByReference(0.0f);
        NativeLong ret = FRLibrary.INSTANCE.AFR_FSDK_FacePairMatching(this.hFREngine, faceFeatureA, faceFeatureB,
                fSimilScore);
        faceFeatureA.freeUnmanaged();
        if (ret.longValue() != 0) {
            log.info(String.format("AFR_FSDK_FacePairMatching failed:ret 0x%x", ret.longValue()));
            return 0.0f;
        }
        log.info(String.format("sim is %f", fSimilScore.getValue()));
        return fSimilScore.getValue();
    }

    public float compareFaceSimilarity(FRFaceModel faceFeatureA, FRFaceModel faceFeatureB) {
        if (faceFeatureA == null) {
            log.error("faceFeatureA is null");
            return 0.0F;
        }
        // calc similarity between faceA and faceB
        FloatByReference fSimilScore = new FloatByReference(0.0f);
        NativeLong ret = FRLibrary.INSTANCE.AFR_FSDK_FacePairMatching(this.hFREngine, faceFeatureA, faceFeatureB,
                fSimilScore);
        // faceFeatureA.freeUnmanaged();
        if (ret.longValue() != 0) {
            log.info(String.format("AFR_FSDK_FacePairMatching failed:ret 0x%x", ret.longValue()));
            return 0.0f;
        }
        // log.info(String.format("sim is %f", fSimilScore.getValue()));
        return fSimilScore.getValue();
    }
}
