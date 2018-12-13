package com.arcsoft.face;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.arcsoft.face.bean.FaceFeature;
import com.arcsoft.face.bean.MultiFaceInfo;
import com.arcsoft.face.bean.SingleFaceInfo;
import com.arcsoft.face.util.EngineUtil;
import com.arcsoft.face.util.ImageLoader;

/**
 * SDK 测试入口类
 *
 * @author Jastar·Wang
 * @version 1.0
 * @date 2018-12-4
 */
public class EngineTest {

	@Test
	public void test() throws IOException {
		InputStream file1 = getClass().getResourceAsStream("/lzl1.jpg");
		URL file2 = getClass().getResource("/lzl2.jpg");

		BufferedImage bufferedImage = ImageIO.read(file1);
		MultiFaceInfo faces = EngineUtil.detectFaces(bufferedImage);

		System.out.println("faces = " + faces);

		SingleFaceInfo face1 = new SingleFaceInfo();
		face1.faceOrient = faces.getFaceOrients()[0];
		face1.faceRect = faces.getFaceRects()[0];
		FaceFeature feature1 = EngineUtil.extractFeature(face1, bufferedImage);

		System.out.println("feature1 = " + feature1);

		// ------------------------//
		// ImageIO.read在加载某些图片时不会正确读取ICC的信息，因此会在写出图片时生成一层红色蒙版
		// BufferedImage bufferedImage2 = ImageIO.read(new
		// File("F:\\pic\\lzl2.jpg"));

		// 使用该种方法加载图片即可解决问题
		Image image = Toolkit.getDefaultToolkit().getImage(file2);
		BufferedImage bufferedImage2 = ImageLoader.toBufferedImage(image);

		MultiFaceInfo faces2 = EngineUtil.detectFaces(bufferedImage2);

		// 写出图片
		// ImageIO.write(bufferedImage2, "jpeg", new
		// FileOutputStream("F:\\pic\\lzl2_rect.jpg", false));

		System.out.println("faces2 = " + faces2);

		SingleFaceInfo face2 = new SingleFaceInfo();
		face2.faceOrient = faces2.getFaceOrients()[0];
		face2.faceRect = faces2.getFaceRects()[0];
		FaceFeature feature2 = EngineUtil.extractFeature(face2, bufferedImage2);

		System.out.println("feature2 = " + feature2);

		System.out.println("similar = " + EngineUtil.compareFeature(feature1, feature2));

	}
}
