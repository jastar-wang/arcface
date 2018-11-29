package com.arcsoft.face.api;

import com.arcsoft.face.AFR_FSDK_FACEINPUT;
import com.arcsoft.face.AFR_FSDK_FACEMODEL;
import com.arcsoft.face.AFR_FSDK_Version;
import com.arcsoft.face.ASVLOFFSCREEN;
import com.arcsoft.face.utils.LoadUtils;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface AFR_FSDKLibrary extends StdCallLibrary {
	AFR_FSDKLibrary INSTANCE = (AFR_FSDKLibrary) LoadUtils.loadLibrary(
			Platform.isWindows() ? "libarcsoft_fsdk_face_recognition.dll" : "libarcsoft_fsdk_face_recognition.so",
			AFR_FSDKLibrary.class);

	NativeLong AFR_FSDK_InitialEngine(String appid, String sdkid, Pointer pMem, int lMemSize,
			PointerByReference phEngine);

	NativeLong AFR_FSDK_ExtractFRFeature(Pointer hEngine, ASVLOFFSCREEN pImgData, AFR_FSDK_FACEINPUT pFaceRes,
			AFR_FSDK_FACEMODEL pFaceModels);

	NativeLong AFR_FSDK_FacePairMatching(Pointer hEngine, AFR_FSDK_FACEMODEL reffeature,
			AFR_FSDK_FACEMODEL probefeature, FloatByReference pfSimilScore);

	NativeLong AFR_FSDK_UninitialEngine(Pointer hEngine);

	AFR_FSDK_Version AFR_FSDK_GetVersion(Pointer hEngine);
}