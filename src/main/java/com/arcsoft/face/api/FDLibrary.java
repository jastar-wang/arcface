package com.arcsoft.face.api;

import com.arcsoft.face.bean.FDVersion;
import com.arcsoft.face.bean.AsvlOffScreen;
import com.arcsoft.face.util.LoadUtils;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface FDLibrary extends StdCallLibrary {
    FDLibrary INSTANCE = (FDLibrary) LoadUtils.loadLibrary(
            Platform.isWindows() ? "libarcsoft_fsdk_face_detection.dll" : "libarcsoft_fsdk_face_detection.so",
            FDLibrary.class);

    NativeLong AFD_FSDK_InitialFaceEngine(String appid, String sdkid, Pointer pMem, int lMemSize,
                                          PointerByReference phEngine, int iOrientPriority, int nScale, int nMaxFaceNum);

    NativeLong AFD_FSDK_StillImageFaceDetection(Pointer hEngine, AsvlOffScreen pImgData, PointerByReference pFaceRes);

    NativeLong AFD_FSDK_UninitialFaceEngine(Pointer hEngine);

    FDVersion AFD_FSDK_GetVersion(Pointer hEngine);
}