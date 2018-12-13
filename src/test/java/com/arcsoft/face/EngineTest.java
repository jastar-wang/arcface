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
	public void testAll() throws IOException {
		// 第一种方式加载图片1
		InputStream file1 = getClass().getResourceAsStream("/lzl1.jpg");
		BufferedImage bufferedImage = ImageIO.read(file1);
		// 检测第1个图片的人脸
		MultiFaceInfo faces = EngineUtil.detectFaces(bufferedImage);

		System.out.println("faces1 = " + faces);

		// 提取第1个人脸的特征值
		SingleFaceInfo face1 = faces.getFaces()[0];
		FaceFeature feature1 = EngineUtil.extractFeature(face1, bufferedImage);

		System.out.println("feature1 = " + feature1);

		// -------------------------------------------------------------//
		// ImageIO.read在加载某些图片时不会正确读取ICC的信息，因此会在写出图片时生成一层红色蒙版，使用下面的方法即可解决该问题
		URL file2 = getClass().getResource("/lzl2.jpg");
		Image image = Toolkit.getDefaultToolkit().getImage(file2);
		BufferedImage bufferedImage2 = ImageLoader.toBufferedImage(image);
		// 检测第2个图片的人脸
		MultiFaceInfo faces2 = EngineUtil.detectFaces(bufferedImage2);

		// 写出带框框的图片到当前根目录
		ImageIO.write(bufferedImage2, "jpeg", new FileOutputStream(new File("lzl2_rect.jpg"), false));

		System.out.println("faces2 = " + faces2);

		// 提取第1个人脸的特征值
		SingleFaceInfo face2 = faces2.getFaces()[0];
		FaceFeature feature2 = EngineUtil.extractFeature(face2, bufferedImage2);

		System.out.println("feature2 = " + feature2);

		// 对比特征值
		System.out.println("similar = " + EngineUtil.compareFeature(feature1, feature2));

		// 销毁引擎
		EngineUtil.uninitEngine();

		System.out.println("已销毁引擎！");
	}
}
