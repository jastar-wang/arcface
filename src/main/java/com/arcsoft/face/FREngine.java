package com.arcsoft.face;

import com.arcsoft.face.api.AFR_FSDKLibrary;
import com.arcsoft.face.api.CLibrary;
import com.arcsoft.face.utils.Linux64Config;
import com.arcsoft.face.utils.Win32Config;
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
			ret = AFR_FSDKLibrary.INSTANCE.AFR_FSDK_InitialEngine(Linux64Config.APPID, Linux64Config.FR_SDKKEY,
					pFRWorkMem, FR_WORKBUF_SIZE, phFREngine);
		} else {
			ret = AFR_FSDKLibrary.INSTANCE.AFR_FSDK_InitialEngine(Win32Config.APPID, Win32Config.FR_SDKKEY, pFRWorkMem,
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
		AFR_FSDK_Version versionFR = AFR_FSDKLibrary.INSTANCE.AFR_FSDK_GetVersion(this.hFREngine);
		return versionFR.Version + '/' + versionFR.BuildDate;
	}

	public void UninitialEngine() {
		AFR_FSDKLibrary.INSTANCE.AFR_FSDK_UninitialEngine(this.hFREngine);
		CLibrary.INSTANCE.free(this.pFRWorkMem);
	}

	public AFR_FSDK_FACEMODEL extractFRFeature(ASVLOFFSCREEN inputImg, FaceInfo faceInfo) {
		AFR_FSDK_FACEINPUT faceinput = new AFR_FSDK_FACEINPUT();
		faceinput.lOrient = faceInfo.orient;
		faceinput.rcFace.left = faceInfo.left;
		faceinput.rcFace.top = faceInfo.top;
		faceinput.rcFace.right = faceInfo.right;
		faceinput.rcFace.bottom = faceInfo.bottom;

		AFR_FSDK_FACEMODEL faceFeature = new AFR_FSDK_FACEMODEL();
		NativeLong ret = AFR_FSDKLibrary.INSTANCE.AFR_FSDK_ExtractFRFeature(this.hFREngine, inputImg, faceinput,
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

	public float compareFaceSimilarity(ASVLOFFSCREEN inputImgA, ASVLOFFSCREEN inputImgB) {
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
		AFR_FSDK_FACEMODEL faceFeatureA = extractFRFeature(inputImgA, faceInfosA[0]);
		if (faceFeatureA == null) {
			log.info("extract face feature in Image A failed");
			return 0.0f;
		}

		AFR_FSDK_FACEMODEL faceFeatureB = extractFRFeature(inputImgB, faceInfosB[0]);
		if (faceFeatureB == null) {
			log.info("extract face feature in Image B failed");
			faceFeatureA.freeUnmanaged();
			return 0.0f;
		}

		// calc similarity between faceA and faceB
		FloatByReference fSimilScore = new FloatByReference(0.0f);
		NativeLong ret = AFR_FSDKLibrary.INSTANCE.AFR_FSDK_FacePairMatching(this.hFREngine, faceFeatureA, faceFeatureB,
				fSimilScore);
		faceFeatureA.freeUnmanaged();
		faceFeatureB.freeUnmanaged();
		if (ret.longValue() != 0) {
			log.info(String.format("AFR_FSDK_FacePairMatching failed:ret 0x%x", ret.longValue()));
			return 0.0f;
		}
		return fSimilScore.getValue();
	}

	public float compareFaceSimilarity(ASVLOFFSCREEN inputImgA, AFR_FSDK_FACEMODEL faceFeatureB) {
		// Do Face Detect
		FaceInfo[] faceInfosA = FDEngine.getInstance().doFaceDetection(inputImgA);
		if (faceInfosA.length < 1) {
			log.info("no face in Image inputImgA ");
			return 0.0f;
		}

		// Extract Face Feature
		AFR_FSDK_FACEMODEL faceFeatureA = extractFRFeature(inputImgA, faceInfosA[0]);
		if (faceFeatureA == null) {
			log.info("extract face feature in Image A failed");
			return 0.0f;
		}

		// calc similarity between faceA and faceB
		FloatByReference fSimilScore = new FloatByReference(0.0f);
		NativeLong ret = AFR_FSDKLibrary.INSTANCE.AFR_FSDK_FacePairMatching(this.hFREngine, faceFeatureA, faceFeatureB,
				fSimilScore);
		faceFeatureA.freeUnmanaged();
		if (ret.longValue() != 0) {
			log.info(String.format("AFR_FSDK_FacePairMatching failed:ret 0x%x", ret.longValue()));
			return 0.0f;
		}
		log.info(String.format("sim is %f", fSimilScore.getValue()));
		return fSimilScore.getValue();
	}

	public float compareFaceSimilarity(AFR_FSDK_FACEMODEL faceFeatureA, AFR_FSDK_FACEMODEL faceFeatureB) {
		if (faceFeatureA == null) {
			log.error("faceFeatureA is null");
			return 0.0F;
		}
		// calc similarity between faceA and faceB
		FloatByReference fSimilScore = new FloatByReference(0.0f);
		NativeLong ret = AFR_FSDKLibrary.INSTANCE.AFR_FSDK_FacePairMatching(this.hFREngine, faceFeatureA, faceFeatureB,
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
