// package com.arcsoft.face;
//
// import com.sun.jna.NativeLong;
// import com.sun.jna.Pointer;
// import com.sun.jna.ptr.PointerByReference;
// import org.bytedeco.javacpp.BytePointer;
// import org.bytedeco.javacpp.opencv_core.IplImage;
//
// import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
//
// public class FeatureExtractTest {
//
//
//     public static void main(String[] args) {
//         FaceLibrary ins = FaceLibrary.INSTANCE;
//         ins.ASFActivation(ConfUtil.appId, ConfUtil.appKey);
//         PointerByReference phEngine = new PointerByReference();
//         int mask = FaceLibrary.ASF_FACE_DETECT | FaceLibrary.ASF_FACERECOGNITION | FaceLibrary.ASF_AGE | FaceLibrary.ASF_GENDER | FaceLibrary.ASF_FACE3DANGLE;
//         NativeLong ret = ins.ASFInitEngine(FaceLibrary.ASF_DETECT_MODE_IMAGE, OrientPriority.AFD_FSDK_OPF_0_HIGHER_EXT, 32, 1, mask, phEngine);
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
//         FaceFeature feature1 = getFeature(ins, hEngine, "e:/pic/zl1.jpg");
//
//         System.out.println(feature1.dump());
//     }
//
//     /**
//      * @param ins FaceLibrary ins = FaceLibrary.INSTANCE
//      * @param hEngine 初始化之後的引擎實例
//      * @param imgPath 圖片路徑
//      * @return 人臉特征
//      */
//     public static FaceFeature getFeature(FaceLibrary ins, Pointer hEngine, String imgPath) {
//         IplImage img = cvLoadImage(imgPath);
//         MultiFaceInfo detectedFaces = new MultiFaceInfo();
//         BytePointer bp = img.arrayData();
//         byte[] imgData = new byte[img.arraySize()];
//         bp.get(imgData);
//         ins.ASFDetectFaces(hEngine, img.width(), img.height(), ColorFormat.ASVL_PAF_RGB24_B8G8R8, imgData, detectedFaces);
//         SingleFaceInfo faceInfo = new SingleFaceInfo();
//         faceInfo.faceRect = detectedFaces.getFaceRects()[0];
//         faceInfo.faceOrient = detectedFaces.getFaceOrients()[0];
//         FaceFeature feature = new FaceFeature();
//         ins.ASFFaceFeatureExtract(hEngine, img.width(), img.height(), ColorFormat.ASVL_PAF_RGB24_B8G8R8, imgData, faceInfo, feature);
//
//         return new FaceFeature(feature.getFeatureData());
//     }
//
// }
