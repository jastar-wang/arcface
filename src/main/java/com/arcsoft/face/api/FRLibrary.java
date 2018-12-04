package com.arcsoft.face.api;

import com.arcsoft.face.bean.FRFaceInput;
import com.arcsoft.face.bean.FRFaceModel;
import com.arcsoft.face.bean.FRVersion;
import com.arcsoft.face.bean.AsvlOffScreen;
import com.arcsoft.face.util.LoadUtils;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface FRLibrary extends StdCallLibrary {
    FRLibrary INSTANCE = (FRLibrary) LoadUtils.loadLibrary(
            Platform.isWindows() ? "libarcsoft_fsdk_face_recognition.dll" : "libarcsoft_fsdk_face_recognition.so",
            FRLibrary.class);

    NativeLong AFR_FSDK_InitialEngine(String appid, String sdkid, Pointer pMem, int lMemSize,
                                      PointerByReference phEngine);

    NativeLong AFR_FSDK_ExtractFRFeature(Pointer hEngine, AsvlOffScreen pImgData, FRFaceInput pFaceRes,
                                         FRFaceModel pFaceModels);

    NativeLong AFR_FSDK_FacePairMatching(Pointer hEngine, FRFaceModel reffeature,
                                         FRFaceModel probefeature, FloatByReference pfSimilScore);

    NativeLong AFR_FSDK_UninitialEngine(Pointer hEngine);

    FRVersion AFR_FSDK_GetVersion(Pointer hEngine);
}