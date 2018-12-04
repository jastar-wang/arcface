package com.arcsoft.face.util;

import com.arcsoft.face.bean.AsvlOffScreen;
import com.arcsoft.face.bean.BufferInfo;
import com.arcsoft.face.enums.ASVLColorFormat;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ImageLoader {
    public static final boolean USING_FLOAT = false;

    public static BufferInfo getI420FromFile(String filePath) {
        byte[] yuv = null;
        int w = 0;
        int h = 0;
        try {
            BufferedImage img = ImageIO.read(new File(filePath));
            if (((img.getWidth() & 0x1) != 0) || ((img.getHeight() & 0x1) != 0)) {
                img = img.getSubimage(0, 0, img.getWidth() & 0xFFFFFFFE, img.getHeight() & 0xFFFFFFFE);
            }
            w = img.getWidth();
            h = img.getHeight();
            int[] bgra = img.getRGB(0, 0, w, h, null, 0, w);
            if (USING_FLOAT) {
                yuv = BGRA2I420_float(bgra, w, h);
            } else {
                yuv = BGRA2I420(bgra, w, h);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return new BufferInfo(w, h, yuv);
    }

    public static BufferInfo getBGRFromFile(String filePath) {
        byte[] bgr = null;
        int width = 0;
        int height = 0;
        try {
            BufferedImage img = ImageIO.read(new File(filePath));
            width = img.getWidth();
            height = img.getHeight();
            BufferedImage bgrimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            bgrimg.setRGB(0, 0, width, height, img.getRGB(0, 0, width, height, null, 0, width), 0, width);
            bgr = ((DataBufferByte) bgrimg.getRaster().getDataBuffer()).getData();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return new BufferInfo(width, height, bgr);
    }

    public static BufferInfo getBGRFromBuffer(BufferedImage img) {
        byte[] bgr = null;
        int width = 0;
        int height = 0;
        width = img.getWidth();
        height = img.getHeight();

        BufferedImage bgrimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

        bgrimg.setRGB(0, 0, width, height, img.getRGB(0, 0, width, height, null, 0, width), 0, width);
        bgr = ((DataBufferByte) bgrimg.getRaster().getDataBuffer()).getData();
        return new BufferInfo(width, height, bgr);
    }

    // Full swing for BT.601
    public static byte[] BGRA2I420(int[] bgra, int width, int height) {

        byte[] yuv = new byte[width * height * 3 / 2];
        int u_offset = width * height;
        int y_offset = width * height * 5 / 4;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb = bgra[i * width + j] & 0x00FFFFFF;
                int b = rgb & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int r = (rgb >> 16) & 0xFF;

                int y = ((77 * r + 150 * g + 29 * b + 128) >> 8);
                int u = (((-43) * r - 84 * g + 127 * b + 128) >> 8) + 128;
                int v = ((127 * r - 106 * g - 21 * b + 128) >> 8) + 128;

                y = y < 0 ? 0 : (y > 255 ? 255 : y);
                u = u < 0 ? 0 : (u > 255 ? 255 : u);
                v = v < 0 ? 0 : (v > 255 ? 255 : v);

                yuv[i * width + j] = (byte) y;
                yuv[u_offset + (i >> 1) * (width >> 1) + (j >> 1)] = (byte) u;
                yuv[y_offset + (i >> 1) * (width >> 1) + (j >> 1)] = (byte) v;
            }
        }
        return yuv;
    }

    // ITU-R standard for YCbCr
    // Y = 0.299R + 0.587G + 0.114B
    // U = -0.169R - 0.331G + 0.499B + 128
    // V = 0.499R - 0.418G - 0.0813B + 128

    public static byte[] BGRA2I420_float(int[] bgra, int width, int height) {

        byte[] yuv = new byte[width * height * 3 / 2];
        int u_offset = width * height;
        int y_offset = width * height * 5 / 4;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb = bgra[i * width + j] & 0x00FFFFFF;
                float b = (rgb & 0xFF);
                float g = ((rgb >> 8) & 0xFF);
                float r = ((rgb >> 16) & 0xFF);

                float y = (0.299f * r + 0.587f * g + 0.114f * b);
                float u = (-0.169f) * r - 0.331f * g + 0.499f * b + 128.0f;
                float v = 0.499f * r - 0.418f * g - 0.0813f * b + 128.0f;

                yuv[i * width + j] = (byte) y;
                yuv[u_offset + (i >> 1) * (width >> 1) + (j >> 1)] = (byte) u;
                yuv[y_offset + (i >> 1) * (width >> 1) + (j >> 1)] = (byte) v;
            }
        }
        return yuv;
    }

    public static AsvlOffScreen loadRAWImage(String yuv_filePath, int yuv_width, int yuv_height, int yuv_format) {
        int yuv_rawdata_size = 0;

        AsvlOffScreen inputImg = new AsvlOffScreen();
        inputImg.u32PixelArrayFormat = yuv_format;
        inputImg.i32Width = yuv_width;
        inputImg.i32Height = yuv_height;
        if (ASVLColorFormat.ASVL_PAF_I420 == inputImg.u32PixelArrayFormat) {
            inputImg.pi32Pitch[0] = inputImg.i32Width;
            inputImg.pi32Pitch[1] = inputImg.i32Width / 2;
            inputImg.pi32Pitch[2] = inputImg.i32Width / 2;
            yuv_rawdata_size = inputImg.i32Width * inputImg.i32Height * 3 / 2;
        } else if (ASVLColorFormat.ASVL_PAF_NV12 == inputImg.u32PixelArrayFormat) {
            inputImg.pi32Pitch[0] = inputImg.i32Width;
            inputImg.pi32Pitch[1] = inputImg.i32Width;
            yuv_rawdata_size = inputImg.i32Width * inputImg.i32Height * 3 / 2;
        } else if (ASVLColorFormat.ASVL_PAF_NV21 == inputImg.u32PixelArrayFormat) {
            inputImg.pi32Pitch[0] = inputImg.i32Width;
            inputImg.pi32Pitch[1] = inputImg.i32Width;
            yuv_rawdata_size = inputImg.i32Width * inputImg.i32Height * 3 / 2;
        } else if (ASVLColorFormat.ASVL_PAF_YUYV == inputImg.u32PixelArrayFormat) {
            inputImg.pi32Pitch[0] = inputImg.i32Width * 2;
            yuv_rawdata_size = inputImg.i32Width * inputImg.i32Height * 2;
        } else if (ASVLColorFormat.ASVL_PAF_RGB24_B8G8R8 == inputImg.u32PixelArrayFormat) {
            inputImg.pi32Pitch[0] = inputImg.i32Width * 3;
            yuv_rawdata_size = inputImg.i32Width * inputImg.i32Height * 3;
        } else {
            log.error("unsupported  yuv format");
            System.exit(0);
        }

        // load YUV Image Data from File
        byte[] imagedata = new byte[yuv_rawdata_size];
        File f = new File(yuv_filePath);
        InputStream ios = null;
        try {
            ios = new FileInputStream(f);
            ios.read(imagedata, 0, yuv_rawdata_size);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("error in loading yuv file");
            System.exit(0);
        } finally {
            try {
                if (ios != null) {
                    ios.close();
                }
            } catch (IOException e) {
            }
        }

        if (ASVLColorFormat.ASVL_PAF_I420 == inputImg.u32PixelArrayFormat) {
            inputImg.ppu8Plane[0] = new Memory(inputImg.pi32Pitch[0] * inputImg.i32Height);
            inputImg.ppu8Plane[0].write(0, imagedata, 0, inputImg.pi32Pitch[0] * inputImg.i32Height);
            inputImg.ppu8Plane[1] = new Memory(inputImg.pi32Pitch[1] * inputImg.i32Height / 2);
            inputImg.ppu8Plane[1].write(0, imagedata, inputImg.pi32Pitch[0] * inputImg.i32Height,
                    inputImg.pi32Pitch[1] * inputImg.i32Height / 2);
            inputImg.ppu8Plane[2] = new Memory(inputImg.pi32Pitch[2] * inputImg.i32Height / 2);
            inputImg.ppu8Plane[2].write(0, imagedata,
                    inputImg.pi32Pitch[0] * inputImg.i32Height + inputImg.pi32Pitch[1] * inputImg.i32Height / 2,
                    inputImg.pi32Pitch[2] * inputImg.i32Height / 2);
            inputImg.ppu8Plane[3] = Pointer.NULL;
        } else if (ASVLColorFormat.ASVL_PAF_NV12 == inputImg.u32PixelArrayFormat) {
            inputImg.ppu8Plane[0] = new Memory(inputImg.pi32Pitch[0] * inputImg.i32Height);
            inputImg.ppu8Plane[0].write(0, imagedata, 0, inputImg.pi32Pitch[0] * inputImg.i32Height);
            inputImg.ppu8Plane[1] = new Memory(inputImg.pi32Pitch[1] * inputImg.i32Height / 2);
            inputImg.ppu8Plane[1].write(0, imagedata, inputImg.pi32Pitch[0] * inputImg.i32Height,
                    inputImg.pi32Pitch[1] * inputImg.i32Height / 2);
            inputImg.ppu8Plane[2] = Pointer.NULL;
            inputImg.ppu8Plane[3] = Pointer.NULL;
        } else if (ASVLColorFormat.ASVL_PAF_NV21 == inputImg.u32PixelArrayFormat) {
            inputImg.ppu8Plane[0] = new Memory(inputImg.pi32Pitch[0] * inputImg.i32Height);
            inputImg.ppu8Plane[0].write(0, imagedata, 0, inputImg.pi32Pitch[0] * inputImg.i32Height);
            inputImg.ppu8Plane[1] = new Memory(inputImg.pi32Pitch[1] * inputImg.i32Height / 2);
            inputImg.ppu8Plane[1].write(0, imagedata, inputImg.pi32Pitch[0] * inputImg.i32Height,
                    inputImg.pi32Pitch[1] * inputImg.i32Height / 2);
            inputImg.ppu8Plane[2] = Pointer.NULL;
            inputImg.ppu8Plane[3] = Pointer.NULL;
        } else if (ASVLColorFormat.ASVL_PAF_YUYV == inputImg.u32PixelArrayFormat) {
            inputImg.ppu8Plane[0] = new Memory(inputImg.pi32Pitch[0] * inputImg.i32Height);
            inputImg.ppu8Plane[0].write(0, imagedata, 0, inputImg.pi32Pitch[0] * inputImg.i32Height);
            inputImg.ppu8Plane[1] = Pointer.NULL;
            inputImg.ppu8Plane[2] = Pointer.NULL;
            inputImg.ppu8Plane[3] = Pointer.NULL;
        } else if (ASVLColorFormat.ASVL_PAF_RGB24_B8G8R8 == inputImg.u32PixelArrayFormat) {
            inputImg.ppu8Plane[0] = new Memory(imagedata.length);
            inputImg.ppu8Plane[0].write(0, imagedata, 0, imagedata.length);
            inputImg.ppu8Plane[1] = Pointer.NULL;
            inputImg.ppu8Plane[2] = Pointer.NULL;
            inputImg.ppu8Plane[3] = Pointer.NULL;
        } else {
            log.error("unsupported yuv format");
            System.exit(0);
        }

        inputImg.setAutoRead(false);
        return inputImg;
    }

    public static AsvlOffScreen loadImage(String filePath, boolean bUseBGRToEngine) {
        AsvlOffScreen inputImg = new AsvlOffScreen();

        if (bUseBGRToEngine) {
            BufferInfo bufferInfo = ImageLoader.getBGRFromFile(filePath);
            inputImg.u32PixelArrayFormat = ASVLColorFormat.ASVL_PAF_RGB24_B8G8R8;
            inputImg.i32Width = bufferInfo.width;
            inputImg.i32Height = bufferInfo.height;
            inputImg.pi32Pitch[0] = inputImg.i32Width * 3;
            inputImg.ppu8Plane[0] = new Memory(inputImg.pi32Pitch[0] * inputImg.i32Height);
            inputImg.ppu8Plane[0].write(0, bufferInfo.buffer, 0, inputImg.pi32Pitch[0] * inputImg.i32Height);
            inputImg.ppu8Plane[1] = Pointer.NULL;
            inputImg.ppu8Plane[2] = Pointer.NULL;
            inputImg.ppu8Plane[3] = Pointer.NULL;
        } else {
            BufferInfo bufferInfo = ImageLoader.getI420FromFile(filePath);
            inputImg.u32PixelArrayFormat = ASVLColorFormat.ASVL_PAF_I420;
            inputImg.i32Width = bufferInfo.width;
            inputImg.i32Height = bufferInfo.height;
            inputImg.pi32Pitch[0] = inputImg.i32Width;
            inputImg.pi32Pitch[1] = inputImg.i32Width / 2;
            inputImg.pi32Pitch[2] = inputImg.i32Width / 2;
            inputImg.ppu8Plane[0] = new Memory(inputImg.pi32Pitch[0] * inputImg.i32Height);
            inputImg.ppu8Plane[0].write(0, bufferInfo.buffer, 0, inputImg.pi32Pitch[0] * inputImg.i32Height);
            inputImg.ppu8Plane[1] = new Memory(inputImg.pi32Pitch[1] * inputImg.i32Height / 2);
            inputImg.ppu8Plane[1].write(0, bufferInfo.buffer, inputImg.pi32Pitch[0] * inputImg.i32Height,
                    inputImg.pi32Pitch[1] * inputImg.i32Height / 2);
            inputImg.ppu8Plane[2] = new Memory(inputImg.pi32Pitch[2] * inputImg.i32Height / 2);
            inputImg.ppu8Plane[2].write(0, bufferInfo.buffer,
                    inputImg.pi32Pitch[0] * inputImg.i32Height + inputImg.pi32Pitch[1] * inputImg.i32Height / 2,
                    inputImg.pi32Pitch[2] * inputImg.i32Height / 2);
            inputImg.ppu8Plane[3] = Pointer.NULL;
        }

        inputImg.setAutoRead(false);
        return inputImg;
    }

    public static AsvlOffScreen loadImage(BufferedImage img) {
        AsvlOffScreen inputImg = new AsvlOffScreen();
        BufferInfo bufferInfo = ImageLoader.getBGRFromBuffer(img);
        inputImg.u32PixelArrayFormat = ASVLColorFormat.ASVL_PAF_RGB24_B8G8R8;
        inputImg.i32Width = bufferInfo.width;
        inputImg.i32Height = bufferInfo.height;
        inputImg.pi32Pitch[0] = inputImg.i32Width * 3;
        inputImg.ppu8Plane[0] = new Memory(inputImg.pi32Pitch[0] * inputImg.i32Height);
        inputImg.ppu8Plane[0].write(0, bufferInfo.buffer, 0, inputImg.pi32Pitch[0] * inputImg.i32Height);
        inputImg.ppu8Plane[1] = Pointer.NULL;
        inputImg.ppu8Plane[2] = Pointer.NULL;
        inputImg.ppu8Plane[3] = Pointer.NULL;

        inputImg.setAutoRead(false);
        return inputImg;
    }

}
