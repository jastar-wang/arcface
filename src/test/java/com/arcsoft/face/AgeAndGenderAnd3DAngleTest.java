// package com.arcsoft.face;
//
// import com.sun.jna.NativeLong;
// import com.sun.jna.Pointer;
// import com.sun.jna.ptr.PointerByReference;
// import org.bytedeco.javacpp.BytePointer;
// import org.bytedeco.javacpp.opencv_core;
// import org.bytedeco.javacpp.opencv_core.IplImage;
// import org.bytedeco.javacpp.opencv_core.Mat;
// import org.bytedeco.javacpp.opencv_core.Rect;
// import org.bytedeco.javacpp.opencv_core.Scalar;
// import org.bytedeco.javacv.OpenCVFrameConverter;
//
// import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
// import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
// import static org.bytedeco.javacpp.opencv_imgproc.*;
//
// /**
//  * 年龄、性别、脸部3d角度测试
//  *
//  * @author tuanj
//  */
// public class AgeAndGenderAnd3DAngleTest {
//
//     public static void main(String[] args) {
//         FaceLibrary ins = FaceLibrary.INSTANCE;
//         ins.ASFActivation(ConfUtil.appId, ConfUtil.appKey);
//         PointerByReference phEngine = new PointerByReference();
//         int mask = FaceLibrary.ASF_FACE_DETECT | FaceLibrary.ASF_FACERECOGNITION | FaceLibrary.ASF_AGE | FaceLibrary.ASF_GENDER | FaceLibrary.ASF_FACE3DANGLE;
//         NativeLong ret = ins.ASFInitEngine(FaceLibrary.ASF_DETECT_MODE_IMAGE, OrientPriority.AFD_FSDK_OPF_0_ONLY, 16, 50, mask, phEngine);
//         if (ret.longValue() != FaceLibrary.MOK) {
//             throw new RuntimeException("init error,code:" + ret.longValue());
//         }
//
//         IplImage img = cvLoadImage("e:/pic/g4.jpg");
//         BytePointer bp = img.arrayData();
//
//         byte[] imgData = new byte[img.arraySize()];
//         bp.get(imgData);
//         System.out.println(img.width());
//         Pointer hEngine = phEngine.getValue();
//
//         Version version = ins.ASFGetVersion(hEngine);
//         System.out.println(version.Version);
//         System.out.println(version.CopyRight);
//         System.out.println(version.BuildDate);
//
//         MultiFaceInfo detectedFaces = new MultiFaceInfo();
//         ins.ASFDetectFaces(hEngine, img.width(), img.height(), ColorFormat.ASVL_PAF_RGB24_B8G8R8, imgData, detectedFaces);
//
//         System.out.println(detectedFaces.getFaceOrients());
//         System.out.println("face num:" + detectedFaces.faceNum);
//
//         int combinedMask = FaceLibrary.ASF_AGE | FaceLibrary.ASF_GENDER | FaceLibrary.ASF_FACE3DANGLE;
//         ins.ASFProcess(hEngine, img.width(), img.height(), ColorFormat.ASVL_PAF_RGB24_B8G8R8, imgData, detectedFaces, combinedMask);
//
//         AgeInfo ageInfo = new AgeInfo();
//         ins.ASFGetAge(hEngine, ageInfo);
//         int[] ages = ageInfo.getAges();
//         System.out.println(ages.length);
//
//         GenderInfo genderInfo = new GenderInfo();
//         ins.ASFGetGender(hEngine, genderInfo);
//         int[] gender = genderInfo.getGenders();
//         System.out.println(gender.length);
//
//         Face3DAngle face3dAngle = new Face3DAngle();
//         ins.ASFGetFace3DAngle(hEngine, face3dAngle);
//         System.out.println(face3dAngle.getStatuses().length);
//
//         //使用opencv的api对照片进行处理，将人脸区域画出来，保存为一张新图片
//         OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
//         Mat mat = converterToMat.convert(converterToMat.convert(img));
//         Rect[] rects = detectedFaces.getFaceRects();
//         for (int i = 0; i < rects.length; i++) {
//             Rect mrect = rects[i];
//             putText(mat, gender[i] + "," + ages[i], new opencv_core.Point(mrect.left, mrect.top - 3), 1, 2, new Scalar(0, 255, 0, 1));
//             rectangle(mat, new Rect(mrect.left, mrect.top, mrect.right - mrect.left, mrect.bottom - mrect.top), new Scalar(0, 255, 0, 1));
//         }
//         imwrite("e:/pic/feture.jpg", mat);
//
//     }
//
// }
