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

/**
 * ArcFace SDK 核心类库
 * 
 * @author Jastar·Wang
 * @date 2018-12-05
 * @since 2.0
 * @license <a href="https://mit-license.org/">MIT</a>
 */
public interface FaceLibrary extends Library {

	FaceLibrary INSTANCE = (FaceLibrary) Native.loadLibrary("libarcsoft_face_engine", FaceLibrary.class);

	/**
	 * 激活SDK<br>
	 * <b>注意：激活后会在本地生成几个“.dat”文件，更换设备并删掉这些文件后，需要重新联网激活</b>
	 *
	 * @param appId
	 *            [in] 官网获取的APPID
	 * @param sdkKey
	 *            [in] 官网获取的SDKKEY
	 * @return 成功返回MOK，失败返回错误码，参考{@link com.arcsoft.face.enums.ErrorCode}
	 */
	NativeLong ASFActivation(String appId, String sdkKey);

	/**
	 * 初始化引擎
	 * 
	 * @param detectMode
	 *            [in] 检测模式
	 *            <li>VIDEO视频模式：适用于摄像头预览，视频文件识别</li>
	 *            <li>IMAGE图片模式：适用于静态图片的识别</li>
	 *            <li>参考{@link com.arcsoft.face.enums.DetectMode}</li>
	 * @param detectFaceOrientPriority
	 *            [in] 检测脸部的角度优先值，参考：
	 *            {@link com.arcsoft.face.enums.OrientPriority},
	 *            {@link com.arcsoft.face.enums.OrientCode}
	 * @param detectFaceScaleVal
	 *            [in]
	 *            用于数值化表示的最小人脸尺寸，该尺寸代表人脸尺寸相对于图片长边的占比。图像数据尺寸为1280x720，设置nscale为8，
	 *            则检测到的最小人脸长边为1280/8 = 160 // 例如，用户想检测到的最小人脸尺寸是图片长边的
	 *            1/8，则scaleVal设置为 8 video 模式有效值范围[2,16], Image
	 *            模式有效值范围[2,32],推荐值为 16
	 * @param detectFaceMaxNum
	 *            [in] 最大需要检测的人脸个数[1,50]
	 * @param combinedMask
	 *            [in] 用户选择需要检测的功能组合，可单个或多个
	 * @param phEngine
	 *            [out] 初始化返回的引擎handle
	 * @return 成功返回MOK，失败返回错误码，参考{@link com.arcsoft.face.enums.ErrorCode}
	 */
	NativeLong ASFInitEngine(int detectMode, int detectFaceOrientPriority, int detectFaceScaleVal, int detectFaceMaxNum,
			int combinedMask, PointerByReference phEngine);

	/**
	 * 人脸检测
	 * 
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
	 * @return 成功返回MOK，失败返回错误码
	 */
	NativeLong ASFDetectFaces(Pointer hEngine, int width, int height, int format, byte[] imgData,
			MultiFaceInfo detectedFaces);

	/**
	 * 单人脸特征提取
	 * 
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
	 * @return 成功返回MOK，失败返回错误码
	 */
	NativeLong ASFFaceFeatureExtract(Pointer hEngine, int width, int height, int format, byte[] imgData,
			SingleFaceInfo faceInfo, FaceFeature feature);

	/**
	 * 人脸特征比对
	 * 
	 * @param hEngine
	 *            [in] 引擎handle
	 * @param feature1
	 *            [in] 待比较人脸特征1
	 * @param feature2
	 *            [in] 待比较人脸特征2
	 * @param confidenceLevel
	 *            [out] 比较结果，置信度数值
	 * @return 成功返回MOK，失败返回错误码
	 */
	NativeLong ASFFaceFeatureCompare(Pointer hEngine, FaceFeature feature1, FaceFeature feature2,
			FloatByReference confidenceLevel);

	/**
	 * 人脸信息检测（年龄/性别/人脸3D角度）,最多支持4张人脸信息检测，超过部分返回未知
	 * 
	 * @param hEngine
	 *            [in] 引擎handle
	 * @param width
	 *            [in] 图片宽度为 4 的倍数且大于 0
	 * @param height
	 *            [in] YUYV/I420/NV21/NV12 格式的图片高度为 2 的倍数，BGR24 格式的图片高度不限制
	 * @param format
	 *            [in] 颜色空间格式
	 * @param imgData
	 *            [in] 图片数据
	 * @param multiFaceInfo
	 *            [in] 人脸信息，用户根据待检测的功能裁减选择需要使用的人脸
	 * @param combinedMask
	 *            [in] 只支持初始化时候指定需要检测的功能，在process时进一步在这个已经指定的功能集中继续筛选；
	 *            例如初始化的时候指定检测年龄和性别， 在process的时候可以只检测年龄， 但是不能检测除年龄和性别之外的功能
	 * @return 成功返回MOK，失败返回错误码
	 */
	NativeLong ASFProcess(Pointer hEngine, int width, int height, int format, byte[] imgData,
			MultiFaceInfo multiFaceInfo, int combinedMask);

	/**
	 * 获得年龄，需要先调用ASFProcess 进行预处理，最高支持4个人脸，多于4张人脸，结果不可靠（官方说法）
	 *
	 * @param hEngine
	 *            [in] 引擎handle
	 * @param ageInfo
	 *            [out] 检测到的年龄信息
	 * @return 成功返回MOK，失败返回错误码
	 */
	NativeLong ASFGetAge(Pointer hEngine, AgeInfo ageInfo);

	/**
	 * 获得性别信息
	 *
	 * @param hEngine
	 *            [in] 引擎handle
	 * @param genderInfo
	 *            [out] 检测到的性别信息
	 * @return 成功返回MOK，失败返回错误码
	 */
	NativeLong ASFGetGender(Pointer hEngine, GenderInfo genderInfo);

	/**
	 * 获得人脸角度数据
	 *
	 * @param hEngine
	 *            [in] 引擎handle
	 * @param face3dAngle
	 *            [out] 检测到脸部3D 角度信息
	 * @return 成功返回MOK，失败返回错误码
	 */
	NativeLong ASFGetFace3DAngle(Pointer hEngine, Face3DAngle face3dAngle);

	/**
	 * 获取版本信息
	 * 
	 * @param hEngine
	 *            [in] 引擎handle
	 * @return 版本信息
	 */
	Version ASFGetVersion(Pointer hEngine);

	/**
	 * 销毁引擎
	 * 
	 * @param hEngine
	 *            [in] 引擎handle
	 * @return 成功返回MOK，失败返回错误码
	 */
	NativeLong ASFUninitEngine(Pointer hEngine);
}
