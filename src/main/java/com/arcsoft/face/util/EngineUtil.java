package com.arcsoft.face.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.arcsoft.face.FaceLibrary;
import com.arcsoft.face.bean.AgeInfo;
import com.arcsoft.face.bean.BufferInfo;
import com.arcsoft.face.bean.Face3DAngle;
import com.arcsoft.face.bean.FaceFeature;
import com.arcsoft.face.bean.GenderInfo;
import com.arcsoft.face.bean.MultiFaceInfo;
import com.arcsoft.face.bean.Rect;
import com.arcsoft.face.bean.SingleFaceInfo;
import com.arcsoft.face.bean.Version;
import com.arcsoft.face.enums.ColorFormat;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.ErrorCode;
import com.arcsoft.face.enums.Mask;
import com.arcsoft.face.enums.OrientPriority;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.PointerByReference;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 引擎工具类
 *
 * @author Jastar·Wang
 * @date 2018-11-30
 * @since 2.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EngineUtil {

	private static FaceLibrary INSTANCE = null;
	private static PointerByReference phEngine = new PointerByReference();

	public static FaceLibrary getInstance() {
		if (INSTANCE == null) {
			synchronized (EngineUtil.class) {
				if (INSTANCE == null) {
					INSTANCE = FaceLibrary.INSTANCE;
					activation();
					init();
				}
			}
		}
		return INSTANCE;
	}

	/**
	 * 激活
	 */
	private static void activation() {
		if (INSTANCE == null) {
			throw new RuntimeException("Face Engine is null");
		}
		NativeLong result = INSTANCE.ASFActivation(ConfUtil.appId, ConfUtil.appKey);

		log.debug("------>engine is activated[{}]!<------", result.longValue());
	}

	/**
	 * 初始化
	 */
	private static void init() {
		int mask = Mask.ASF_FACE_DETECT | Mask.ASF_FACERECOGNITION | Mask.ASF_AGE | Mask.ASF_GENDER
				| Mask.ASF_FACE3DANGLE;
		NativeLong ret = INSTANCE.ASFInitEngine(DetectMode.ASF_DETECT_MODE_IMAGE, OrientPriority.AFD_FSDK_OPF_0_ONLY,
				16, 50, mask, phEngine);
		if (ret.longValue() != ErrorCode.MOK) {
			throw new RuntimeException("engine init error by code :" + ret.longValue());
		}
		log.debug("------>engine init finish!<------");
	}

	/**
	 * 剪裁图片以获得4的整数倍宽度（官方要求，原因尚不清楚）
	 *
	 * @param src
	 *            原图
	 * @return 新图
	 */
	private static BufferedImage convertImageTo4Times(BufferedImage src) {
		if (src != null && src.getWidth() % 4 != 0) {
			return src.getSubimage(0, 0, src.getWidth() - (src.getWidth() % 4), src.getHeight());
		}
		return src;
	}

	/**
	 * 获取引擎版本信息
	 *
	 * @return 引擎版本信息
	 */
	public static Version getEngineVersion() {
		return getInstance().ASFGetVersion(phEngine.getValue());
	}

	/**
	 * 
	 * 检测人脸信息
	 * 
	 * @param image
	 *            要检测的图片
	 * @return 多张人脸信息
	 */
	public static MultiFaceInfo detectFaces(BufferedImage image) {
		MultiFaceInfo detectFaces = new MultiFaceInfo();
		if (image == null) {
			return detectFaces;
		}
		image = convertImageTo4Times(image);
		BufferInfo bufferInfo = ImageLoader.getBGRFromFile(image);
		getInstance().ASFDetectFaces(phEngine.getValue(), image.getWidth(), image.getHeight(),
				ColorFormat.ASVL_PAF_RGB24_B8G8R8, bufferInfo.buffer, detectFaces);

		// 在图片中画出人脸框
		if (detectFaces.haveFace()) {
			Rect[] mrects = detectFaces.getFaceRects();
			for (Rect mrect : mrects) {
				Graphics g = image.getGraphics();
				g.setColor(Color.RED);
				g.drawRect(mrect.getLeft(), mrect.getTop(), mrect.getWidth(), mrect.getHeight());
				// TODO 此处没有释放g资源，画线的操作可根据自身需求进行修改
			}
		}
		return detectFaces;
	}

	/**
	 * 提取特征值
	 *
	 * @param faceInfo
	 *            单人脸信息
	 * @param image
	 *            图片
	 * @return 特征值对象
	 */
	public static FaceFeature extractFeature(SingleFaceInfo faceInfo, BufferedImage image) {
		FaceFeature feature = new FaceFeature();
		image = convertImageTo4Times(image);
		BufferInfo bufferInfo = ImageLoader.getBGRFromFile(image);
		getInstance().ASFFaceFeatureExtract(phEngine.getValue(), image.getWidth(), image.getHeight(),
				ColorFormat.ASVL_PAF_RGB24_B8G8R8, bufferInfo.buffer, faceInfo, feature);
		if (feature.getFeatureData() != null && feature.getFeatureData().length > 0) {
			// 注意此处返回的时候重新new了一个新的FaceFeature，此处的目的是对FaceFeature做深度Copy，因为虹软对内存做了一些优化，FaceFeature的内存会被重复使用，如果不做深度copy，反复调用该方法，FaceFeature中的特征数据会被覆盖
			return new FaceFeature(feature.getFeatureData());
		}
		return null;
	}

	/**
	 * 对比特征值
	 *
	 * @param feature1
	 *            特征值1
	 * @param feature2
	 *            特征值2
	 * @return 相似度（置信度）
	 */
	public static float compareFeature(FaceFeature feature1, FaceFeature feature2) {
		FloatByReference similar = new FloatByReference();
		getInstance().ASFFaceFeatureCompare(phEngine.getValue(), feature1, feature2, similar);
		return similar.getValue();
	}

	/**
	 * 销毁引擎
	 * 
	 * @return 状态码
	 */
	public static long uninitEngine() {
		NativeLong result = getInstance().ASFUninitEngine(phEngine.getValue());
		return result.longValue();
	}

	/**
	 * 预处理检测年龄、性别、3D角度信息<br>
	 * 该方法适用于调用前未提前检测人脸对象，内部根据image重新检测；<b>注意：最多支持4张人脸检测，超过部分返回未知</b>
	 * 
	 * @param image
	 *            图片对象
	 * @return 状态码
	 */
	public static long process(BufferedImage image) {
		MultiFaceInfo faces = detectFaces(image);
		return process(faces, image);
	}

	/**
	 * 预处理检测年龄、性别、3D角度<br>
	 * 该方法适用于调用前已经检测了人脸，内部不再检测，提升性能；<b>注意：最多支持4张人脸检测，超过部分返回未知</b>
	 * 
	 * @param faceInfo
	 *            人脸信息
	 * @param image
	 *            图片
	 */
	public static long process(MultiFaceInfo faceInfo, BufferedImage image) {
		image = convertImageTo4Times(image);
		BufferInfo bufferInfo = ImageLoader.getBGRFromFile(image);
		int combinedMask = Mask.ASF_AGE | Mask.ASF_GENDER | Mask.ASF_FACE3DANGLE;
		NativeLong result = getInstance().ASFProcess(phEngine.getValue(), image.getWidth(), image.getHeight(),
				ColorFormat.ASVL_PAF_RGB24_B8G8R8, bufferInfo.buffer, faceInfo, combinedMask);
		return result.longValue();
	}

	/**
	 * 获得人脸年龄<br>
	 * <b>调用前必须先调用 {@link com.arcsoft.face.util.EngineUtil#process} ，且只需调用一次</b>
	 * 
	 * @return 年龄信息
	 */
	public static AgeInfo getAge() {
		AgeInfo ageInfo = new AgeInfo();
		getInstance().ASFGetAge(phEngine.getValue(), ageInfo);
		return ageInfo;
	}

	/**
	 * 获得性别<br>
	 * <b>调用前必须先调用 {@link com.arcsoft.face.util.EngineUtil#process} ，且只需调用一次</b>
	 * 
	 * @return 性别信息
	 */
	public static GenderInfo getGender() {
		GenderInfo gender = new GenderInfo();
		getInstance().ASFGetGender(phEngine.getValue(), gender);
		return gender;
	}

	/**
	 * 获得人脸3D角度信息<br>
	 * <b>调用前必须先调用 {@link com.arcsoft.face.util.EngineUtil#process} ，且只需调用一次</b>
	 * 
	 * @return 3D角度信息
	 */
	public static Face3DAngle getFace3DAngle() {
		Face3DAngle face3dAngle = new Face3DAngle();
		getInstance().ASFGetFace3DAngle(phEngine.getValue(), face3dAngle);
		return face3dAngle;
	}

}
