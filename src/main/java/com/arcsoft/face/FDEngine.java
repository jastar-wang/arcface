package com.arcsoft.face;

import com.arcsoft.face.api.AFD_FSDKLibrary;
import com.arcsoft.face.api.CLibrary;
import com.arcsoft.face.bean.AFD_FSDK_Version;
import com.arcsoft.face.bean.ASVLOFFSCREEN;
import com.arcsoft.face.bean.FaceInfo;
import com.arcsoft.face.bean.MRECT;
import com.arcsoft.face.util.Linux64Config;
import com.arcsoft.face.util.Win32Config;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FDEngine {
	private static FDEngine engin = new FDEngine();
	private static final int FD_WORKBUF_SIZE = 20 * 1024 * 1024;
	private static final int MAX_FACE_NUM = 50;
	private Pointer hFDEngine;
	private Pointer pFDWorkMem;

	public static FDEngine getInstance() {
		return engin;
	}

	private FDEngine() {
		boolean isLinux64 = System.getProperty("os.name").equals("Linux")
				&& System.getProperty("os.arch").equals("amd64");

		this.pFDWorkMem = CLibrary.INSTANCE.malloc(FD_WORKBUF_SIZE);
		PointerByReference phFDEngine = new PointerByReference();

		NativeLong ret;
		if (isLinux64) {
			ret = AFD_FSDKLibrary.INSTANCE.AFD_FSDK_InitialFaceEngine(Linux64Config.APPID, Linux64Config.FD_SDKKEY,
					pFDWorkMem, FD_WORKBUF_SIZE, phFDEngine, AFD_FSDK_OrientPriority.AFD_FSDK_OPF_0_HIGHER_EXT, 32,
					MAX_FACE_NUM);
		} else {
			ret = AFD_FSDKLibrary.INSTANCE.AFD_FSDK_InitialFaceEngine(Win32Config.APPID, Win32Config.FD_SDKKEY,
					pFDWorkMem, FD_WORKBUF_SIZE, phFDEngine, AFD_FSDK_OrientPriority.AFD_FSDK_OPF_0_HIGHER_EXT, 32,
					MAX_FACE_NUM);
		}
		if (ret.longValue() != 0) {
			CLibrary.INSTANCE.free(pFDWorkMem);
			log.error(String.format("AFD_FSDK_InitialFaceEngine ret 0x%x", ret.longValue()));
			return;
		}
		this.hFDEngine = phFDEngine.getValue();

	}

	public String getVersion() {
		AFD_FSDK_Version versionFD = AFD_FSDKLibrary.INSTANCE.AFD_FSDK_GetVersion(hFDEngine);
		return versionFD.Version + '/' + versionFD.BuildDate;
	}

	public FaceInfo[] doFaceDetection(ASVLOFFSCREEN inputImg) {
		FaceInfo[] faceInfo = new FaceInfo[0];

		PointerByReference ppFaceRes = new PointerByReference();
		NativeLong ret = new NativeLong();
		try {
			ret = AFD_FSDKLibrary.INSTANCE.AFD_FSDK_StillImageFaceDetection(this.hFDEngine, inputImg, ppFaceRes);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		if (ret.longValue() != 0) {
			log.warn(String.format("AFD_FSDK_StillImageFaceDetection ret 0x%x", ret.longValue()));
			return faceInfo;
		}

		AFD_FSDK_FACERES faceRes = new AFD_FSDK_FACERES(ppFaceRes.getValue());
		if (faceRes.nFace > 0) {
			faceInfo = new FaceInfo[faceRes.nFace];
			for (int i = 0; i < faceRes.nFace; i++) {
				MRECT rect = new MRECT(
						new Pointer(Pointer.nativeValue(faceRes.rcFace.getPointer()) + faceRes.rcFace.size() * i));
				int orient = faceRes.lfaceOrient.getPointer().getInt(i * 4);
				faceInfo[i] = new FaceInfo();

				faceInfo[i].left = rect.left;
				faceInfo[i].top = rect.top;
				faceInfo[i].right = rect.right;
				faceInfo[i].bottom = rect.bottom;
				faceInfo[i].orient = orient;

				// System.out.println(String.format("%d (%d %d %d %d) orient
				// %d", i, rect.left, rect.top, rect.right, rect.bottom,
				// orient));
			}
		}
		return faceInfo;
	}

	public void UninitialEngine() {
		AFD_FSDKLibrary.INSTANCE.AFD_FSDK_UninitialFaceEngine(this.hFDEngine);
		CLibrary.INSTANCE.free(this.pFDWorkMem);
	}

}
