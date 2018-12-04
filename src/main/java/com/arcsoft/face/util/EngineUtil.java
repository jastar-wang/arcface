package com.arcsoft.face.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.arcsoft.face.FaceLibrary;
import com.arcsoft.face.bean.BufferInfo;
import com.arcsoft.face.bean.FaceFeature;
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

import lombok.extern.slf4j.Slf4j;

/**
 * 引擎入口工具类
 *
 * @author Jastar·Wang
 * @version 1.0
 * @date 2018/11/30
 */
@Slf4j
public class EngineUtil {

	private static FaceLibrary INSTANCE = null;
	private static PointerByReference phEngine = new PointerByReference();

	public static FaceLibrary getInstance() {
		if (INSTANCE == null) {
			INSTANCE = FaceLibrary.INSTANCE;
			activation();
			init();
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
	 * 将图片的宽度转换为4的整数倍（官方要求，原因尚不清楚）
	 *
	 * @param src
	 *            原图
	 * @return 新图
	 */
	private static BufferedImage convertImageTo4Times(BufferedImage src) {
		if (src.getWidth() % 4 != 0) {
			return src.getSubimage(0, 0, src.getWidth() - (src.getWidth() % 4), src.getHeight());
		}
		return src;
	}

	/**
	 * 获取引擎版本信息
	 *
	 * @author Jastar Wang
	 * @date 2018/11/30
	 * @version 1.0
	 * @reurn 引擎版本信息
	 */
	public static Version getEngineVersion() {
		return getInstance().ASFGetVersion(phEngine.getValue());
	}

	/**
	 * 检测多张人脸
	 *
	 * @param image
	 *            图片
	 * @author Jastar Wang
	 * @date 2018/11/30
	 * @version 1.0
	 */
	public static MultiFaceInfo detectFaces(BufferedImage image) {
		MultiFaceInfo detectFaces = new MultiFaceInfo();
		image = convertImageTo4Times(image);
		BufferInfo bufferInfo = ImageLoader.getBGRFromFile(image);
		getInstance().ASFDetectFaces(phEngine.getValue(), image.getWidth(), image.getHeight(),
				ColorFormat.ASVL_PAF_RGB24_B8G8R8, bufferInfo.buffer, detectFaces);

		if (detectFaces.haveFace()) {
			Rect[] mrects = detectFaces.getFaceRects();
			for (Rect mrect : mrects) {
				Graphics g = image.getGraphics();
				g.setColor(Color.RED);
				g.drawRect(mrect.left, mrect.top, mrect.right - mrect.left, mrect.bottom - mrect.top);
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
	 * @author Jastar Wang
	 * @date 2018/11/30
	 * @version 1.0
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
	 * @author Jastar Wang
	 * @date 2018/11/30
	 * @version 1.0
	 */
	public static float compareFeature(FaceFeature feature1, FaceFeature feature2) {
		FloatByReference similar = new FloatByReference();
		getInstance().ASFFaceFeatureCompare(phEngine.getValue(), feature1, feature2, similar);
		return similar.getValue();
	}

	// public static void main(String[] args) throws FrameGrabber.Exception,
	// InterruptedException {
	// OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
	// grabber.start(); //开始获取摄像头数据
	// CanvasFrame canvas = new CanvasFrame("摄像头");//新建一个窗口
	// canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	// canvas.setAlwaysOnTop(true);
	// while (true) {
	// if (!canvas.isDisplayable()) {//窗口是否关闭
	// grabber.stop();//停止抓取
	// System.exit(-1);//退出
	// }
	//
	// Frame frame = grabber.grab();
	//
	// canvas.showImage(frame);//获取摄像头图像并放到窗口上显示， 这里的Frame frame=grabber.grab();
	// frame是一帧视频图像
	// Thread.sleep(50);//50毫秒刷新一次图像
	// }
	// }

}
