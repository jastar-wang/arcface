package com.jastarwang.test;

import com.arcsoft.face.FDEngine;
import com.arcsoft.face.FREngine;
import com.arcsoft.face.bean.AFR_FSDK_FACEMODEL;
import com.arcsoft.face.bean.ASVLOFFSCREEN;
import com.arcsoft.face.bean.FaceInfo;
import com.arcsoft.face.util.ImageLoader;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * SDK测试类
 *
 * @author Jastar Wang
 * @version 1.0
 * @date 2018/12/4
 */
public class EngineTest {

    FDEngine fdEngine = FDEngine.getInstance();
    FREngine frEngine = FREngine.getInstance();

    @Test
    public void test() throws IOException {
        // 文件
        InputStream file1 = getClass().getResourceAsStream("/lzl1.jpg");
        InputStream file2 = getClass().getResourceAsStream("/lzl2.jpg");

        AFR_FSDK_FACEMODEL mode1 = getFaceModel(file1);
        AFR_FSDK_FACEMODEL mode2 = getFaceModel(file2);

        /**
         * 对比相似度
         */
        float similarity = frEngine.compareFaceSimilarity(mode1, mode2);
        System.out.println("相似度 = " + similarity);
    }

    /**
     * 根据图片提取人脸特征值
     *
     * @param inputStream 图片输入流
     * @return 人脸特征值对象
     * @throws IOException
     * @author Jastar Wang
     * @date 2018/12/4
     * @version 1.0
     */
    private AFR_FSDK_FACEMODEL getFaceModel(InputStream inputStream) throws IOException {
        /**
         * 加载图片
         */
        BufferedImage img = ImageIO.read(inputStream);
        ASVLOFFSCREEN image = ImageLoader.loadImage(img);

        /**
         * 检测人脸
         */
        FaceInfo[] faces = fdEngine.doFaceDetection(image);
        if (faces == null || faces.length <= 0) {
            System.out.println("未检测到人脸");
            return null;
        }

        /**
         * 提取特征值（此处演示只使用第一个，实际情况根据业务操作）
         */
        AFR_FSDK_FACEMODEL faceModel = frEngine.extractFRFeature(image, faces[0]);
        System.out.println("检测到了人脸：" + faceModel);
        return faceModel;
    }

}
