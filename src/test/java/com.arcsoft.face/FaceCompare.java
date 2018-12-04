// package com.arcsoft.face;
//
// import com.sun.jna.NativeLong;
// import com.sun.jna.Pointer;
// import com.sun.jna.ptr.FloatByReference;
// import com.sun.jna.ptr.PointerByReference;
//
// public class FaceCompare {
//
//     public static void main(String[] args) {
//         FaceLibrary ins = FaceLibrary.INSTANCE;
//         ins.ASFActivation(ConfUtil.appId, ConfUtil.appKey);
//         PointerByReference phEngine = new PointerByReference();
//         int mask = FaceLibrary.ASF_FACE_DETECT | FaceLibrary.ASF_FACERECOGNITION | FaceLibrary.ASF_AGE | FaceLibrary.ASF_GENDER | FaceLibrary.ASF_FACE3DANGLE;
//         NativeLong ret = ins.ASFInitEngine(FaceLibrary.ASF_DETECT_MODE_IMAGE, OrientPriority.AFD_FSDK_OPF_0_ONLY, 16, 50, mask, phEngine);
//         if (ret.longValue() != FaceLibrary.MOK) {
//             System.out.println(ret.longValue());
//         }
//
//
//         Pointer hEngine = phEngine.getValue();
//
//         Version version = ins.ASFGetVersion(hEngine);
//         System.out.println(version.Version);
//         System.out.println(version.CopyRight);
//         System.out.println(version.BuildDate);
//
//         FaceFeature feature1 = FeatureExtractTest.getFeature(ins, hEngine, "e:/pic/zl1.jpg");
//         FaceFeature feature2 = FeatureExtractTest.getFeature(ins, hEngine, "e:/pic/zl3"
//                 + ".jpg");
//
//         FloatByReference confidenceLevel = new FloatByReference();
//         ins.ASFFaceFeatureCompare(hEngine, feature1, feature2, confidenceLevel);
//         System.out.println(confidenceLevel.getValue());
//     }
//
// }
