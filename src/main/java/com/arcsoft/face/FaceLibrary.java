package com.arcsoft.face;

import com.arcsoft.face.bean.AgeInfo;
import com.arcsoft.face.bean.Face3DAngle;
import com.arcsoft.face.bean.FaceFeature;
import com.arcsoft.face.bean.GenderInfo;
import com.arcsoft.face.bean.MultiFaceInfo;
import com.arcsoft.face.bean.SingleFaceInfo;
import com.arcsoft.face.bean.Version;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.PointerByReference;

public interface FaceLibrary extends Library {

	FaceLibrary INSTANCE = (FaceLibrary) Native.loadLibrary("libarcsoft_face_engine", FaceLibrary.class);

	// 激活
	NativeLong ASFActivation(String appId, String sdkKey);

	/**
	 * @param detectMode
	 *            [in] VIDEO 模式/IMAGE 模式 VIDEO 模式:处理连续帧的图像数据，并返回检测结果，需要将所有图
	 *            像帧的数据都传入接口进行处理; IMAGE 模式:处理单帧的图像数据，并返回检测结果
	 * @param detectFaceOrientPriority
	 *            [in] 检测脸部的角度优先值，推荐仅检测单一角度，效果更优
	 * @param detectFaceScaleVal
	 *            [in] 用于数值化表示的最小人脸尺寸，该尺寸代表人脸尺寸相对于图片长 边的占比。 video 模式有效值范围[2,16],
	 *            Image 模式有效值范围[2,32] 推荐值为 16
	 * @param detectFaceMaxNum
	 *            [in] 最大需要检测的人脸个数[1-50]
	 * @param combinedMask
	 *            [in] 用户选择需要检测的功能组合，可单个或多个
	 * @param phEngine
	 *            [out] 初始化返回的引擎 handle
	 * @return
	 */
	NativeLong ASFInitEngine(int detectMode, int detectFaceOrientPriority, int detectFaceScaleVal, int detectFaceMaxNum,
			int combinedMask, PointerByReference phEngine);

	/**
	 * @param hEngine
	 *            [in] 引擎 handle
	 * @param width
	 *            [in] 图片宽度为 4 的倍数且大于 0
	 * @param height
	 *            [in] YUYV/I420/NV21/NV12 格式的图片高度为 2 的倍数，BGR24 格式的图片高度不限制
	 * @param format
	 *            [in] 颜色空间格式
	 * @param imgData
	 *            [in] 图片数据
	 * @param detectedFaces
	 *            [out] 检测到的人脸信息
	 * @return
	 */
	NativeLong ASFDetectFaces(Pointer hEngine, int width, int height, int format, byte[] imgData,
			MultiFaceInfo detectedFaces);

	/**
	 * @param hEngine
	 *            [in] 引擎 handle
	 * @param width
	 *            [in] 图片宽度为 4 的倍数且大于 0
	 * @param height
	 *            [in] YUYV/I420/NV21/NV12 格式的图片高度为 2 的倍数，BGR24 格式的图片高度不限制
	 * @param format
	 *            [in] 颜色空间格式
	 * @param imgData
	 *            [in] 图片数据
	 * @param faceInfo
	 *            [in] 单张人脸位置和角度信息
	 * @param feature
	 *            [out] 人脸特征
	 * @return
	 */
	NativeLong ASFFaceFeatureExtract(Pointer hEngine, int width, int height, int format, byte[] imgData,
			SingleFaceInfo faceInfo, FaceFeature feature);

	/**
	 * @param hEngine
	 * @param feature1
	 * @param feature2
	 * @param confidenceLevel
	 *            [out] 比对结果，置信度数值
	 * @return
	 */
	NativeLong ASFFaceFeatureCompare(Pointer hEngine, FaceFeature feature1, FaceFeature feature2,
			FloatByReference confidenceLevel);

	/**
	 * @param hEngine
	 *            [in]
	 * @param width
	 *            [in]
	 * @param height
	 *            [in]
	 * @param format
	 *            [in]
	 * @param imgData
	 *            [in]
	 * @param multiFaceInfo
	 *            [in]
	 * @param combinedMask
	 *            [in] 初始化中参数 combinedMask 与 ASF_AGE| ASF_FACE3DANGLE 的交集的子集
	 * @return 成功返回 MOK，否则返回失败 codes。
	 */
	NativeLong ASFProcess(Pointer hEngine, int width, int height, int format, byte[] imgData,
			MultiFaceInfo multiFaceInfo, int combinedMask);

	/**
	 * 获得年龄，需要先调用ASFProcess 进行预处理，最高支持4个人脸，多于4张人脸，结果不可靠（官方说法）
	 *
	 * @param hEngine
	 * @param ageInfo
	 * @return
	 */
	NativeLong ASFGetAge(Pointer hEngine, AgeInfo ageInfo);

	/**
	 * 获得性别信息
	 *
	 * @param hEngine
	 * @param genderInfo
	 * @return
	 */
	NativeLong ASFGetGender(Pointer hEngine, GenderInfo genderInfo);

	/**
	 * 获得人脸角度数据
	 *
	 * @param hEngine
	 * @param face3dAngle
	 * @return
	 */
	NativeLong ASFGetFace3DAngle(Pointer hEngine, Face3DAngle face3dAngle);

	Version ASFGetVersion(Pointer hEngine);

	NativeLong ASFUninitEngine(Pointer hEngine);
}
