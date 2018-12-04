// package com.arcsoft.face;
//
// import com.sun.jna.NativeLong;
// import com.sun.jna.Pointer;
// import com.sun.jna.ptr.PointerByReference;
// import org.bytedeco.javacpp.BytePointer;
// import org.bytedeco.javacpp.opencv_core.*;
// import org.bytedeco.javacv.OpenCVFrameConverter;
//
// import static org.bytedeco.javacpp.opencv_core.*;
// import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
// import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
// import static org.bytedeco.javacpp.opencv_imgproc.rectangle;
//
// public class FaceDetectTest {
//
//     public static void main(String[] args) {
//         FaceLibrary ins = FaceLibrary.INSTANCE;
//
//         //SDK激活
//         ins.ASFActivation(ConfUtil.appId, ConfUtil.appKey);
//         PointerByReference phEngine = new PointerByReference();
//
//         //SDK初始化，需要设置模式是video，还是image，配置人脸检测的角度，engine需要支持的功能 mask，最大支持的人脸数量50，人脸大小16
//         int mask = FaceLibrary.ASF_FACE_DETECT | FaceLibrary.ASF_FACERECOGNITION | FaceLibrary.ASF_AGE | FaceLibrary.ASF_GENDER | FaceLibrary.ASF_FACE3DANGLE;
//         NativeLong ret = ins.ASFInitEngine(FaceLibrary.ASF_DETECT_MODE_IMAGE, OrientPriority.AFD_FSDK_OPF_0_ONLY, 16, 10, mask, phEngine);
//         if (ret.longValue() != FaceLibrary.MOK) {
//             System.out.println(ret.longValue());
//         }
//
//         //获取图片数据，此处使用javacv中封装的对应opencv的方法完成
//         IplImage img = cvLoadImage("e:/pic/face0.jpg");
//         int width = img.width() - img.width() % 4;
//         int height = img.height() - img.height() % 2;
//         img = cutImg(img, new CvRect(0, 0, width, height));
//         BytePointer bp = img.arrayData();
//         byte[] imgData = new byte[img.arraySize()];//图片数据
//         bp.get(imgData);
//
//         //获得engine对应的指针
//         Pointer hEngine = phEngine.getValue();
//
//         //当前系统版本
//         Version version = ins.ASFGetVersion(hEngine);
//         System.out.println(version.Version);
//         System.out.println(version.CopyRight);
//         System.out.println(version.BuildDate);
//
//         //进行人脸检测，detectedFaces为输出项
//         MultiFaceInfo detectedFaces = new MultiFaceInfo();
//         ins.ASFDetectFaces(hEngine, img.width(), img.height(), ColorFormat.ASVL_PAF_RGB24_B8G8R8, imgData, detectedFaces);
//
//         System.out.println("detected face num:" + detectedFaces.faceNum);
//
//         //使用opencv的api对照片进行处理，将人脸区域画出来，保存为一张新图片
//         OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
//         Mat mat = converterToMat.convert(converterToMat.convert(img));
//         Rect[] rects = detectedFaces.getFaceRects();
//         for (Rect mrect : rects) {
//             rectangle(mat, new Rect(mrect.left, mrect.top, mrect.right - mrect.left, mrect.bottom - mrect.top), new Scalar(0, 255, 0, 1));
//         }
//         imwrite("e:/pic/detected.jpg", mat);
//     }
//
//     public static IplImage cutImg(IplImage inputImage, CvRect rect) {
//         IplImage dst = cvCreateImage(cvSize(rect.width(), rect.height()), IPL_DEPTH_8U, inputImage.nChannels());
//         if (rect.width() <= inputImage.width() && rect.height() <= inputImage.height() && rect.x() >= 0 && rect.y() >= 0 && rect.width() >= 0 && rect.height() >= 0) {
//             cvSetImageROI(inputImage, rect);
//             cvCopy(inputImage, dst);
//             return dst;
//         } else {
//             return inputImage;
//         }
//     }
//
//
// }
