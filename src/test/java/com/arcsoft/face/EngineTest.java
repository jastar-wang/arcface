package com.arcsoft.face;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;

import com.arcsoft.face.bean.FaceFeature;
import com.arcsoft.face.bean.MultiFaceInfo;
import com.arcsoft.face.bean.SingleFaceInfo;
import com.arcsoft.face.bean.Version;
import com.arcsoft.face.util.EngineUtil;
import com.arcsoft.face.util.ImageLoader;

/**
 * SDK 测试入口类
 *
 * @author Jastar·Wang
 * @date 2018-12-4
 * @since 2.0
 */
public class EngineTest {

	@Test
	public void getInstance() {
		FaceLibrary instance = EngineUtil.getInstance();
		Assert.assertNotNull(instance);
	}

	@Test
	public void getEngineVersion() {
		Version version = EngineUtil.getEngineVersion();
		System.out.println(version);
	}

	/**
	 * 检测人脸、提取人脸特征值、对比特征值测试
	 * 
	 * @throws IOException
	 */
	@Test
	public void testFace() throws IOException {
		// 1、加载图片1（使用方式一加载图片）
		InputStream file1 = getClass().getResourceAsStream("/lzl1.jpg");
		BufferedImage bufferedImage = ImageIO.read(file1);
		// 2、检测第1个图片的人脸
		MultiFaceInfo faces = EngineUtil.detectFaces(bufferedImage);
		// 3、提取第1个人脸的特征值
		SingleFaceInfo face1 = faces.getFaces()[0];
		FaceFeature feature1 = EngineUtil.extractFeature(face1, bufferedImage);

		System.out.println("faces1 = " + faces);
		System.out.println("feature1 = " + feature1);
		System.out.println("--------------------分割线---------------------");

		// 4、加载图片2（使用以下方式加载[仅限windows]，第一种方式ImageIO.read在加载某些图片时不会正确读取ICC的信息，因此会在写出图片时生成一层红色蒙版）
		BufferedImage bufferedImage2 = loadImage("/lzl2.jpg");
		// 5、检测第2个图片的人脸
		MultiFaceInfo faces2 = EngineUtil.detectFaces(bufferedImage2);
		// 5.1、写出带框框的图片到当前根目录
		ImageIO.write(bufferedImage2, "jpeg", new FileOutputStream(new File("lzl2_rect.jpg"), false));
		// 6、提取第1个人脸的特征值
		SingleFaceInfo face2 = faces2.getFaces()[0];
		FaceFeature feature2 = EngineUtil.extractFeature(face2, bufferedImage2);

		System.out.println("faces2 = " + faces2);
		System.out.println("feature2 = " + feature2);

		// 7、对比特征值
		System.out.println("similar = " + EngineUtil.compareFeature(feature1, feature2));
		// 8、销毁引擎
		EngineUtil.uninitEngine();

		System.out.println("已销毁引擎！");
	}

	/**
	 * 测试年龄性别人脸3D角度信息
	 */
	@Test
	public void testAgeGender3DAngle() {
		BufferedImage image1 = loadImage("/object.jpg");
		EngineUtil.process(image1);
		System.out.println("--->第一个：");
		System.out.println(EngineUtil.getAge());
		System.out.println(EngineUtil.getGender());
		System.out.println(EngineUtil.getFace3DAngle());

		BufferedImage image2 = loadImage("/nanshen.jpg");
		EngineUtil.process(image2);
		System.out.println("--->第二个：");
		System.out.println(EngineUtil.getAge());
		System.out.println(EngineUtil.getGender());
		System.out.println(EngineUtil.getFace3DAngle());
	}

	/**
	 * 加载图片
	 * 
	 * @param relativePath
	 * @return
	 */
	private BufferedImage loadImage(String relativePath) {
		URL file2 = getClass().getResource(relativePath);
		Image image = Toolkit.getDefaultToolkit().getImage(file2);
		return ImageLoader.toBufferedImage(image);
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
