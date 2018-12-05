package com.arcsoft.face.bean;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

import lombok.ToString;

/**
 * 多人脸
 * 
 * @author Jastar·Wang
 * @email jastar_wang@163.com
 * @date 2018-12-05
 * @since 2.0
 */
@ToString
public class MultiFaceInfo extends Structure {

	public Rect.ByReference faceRect;
	public IntByReference faceOrient;
	public int faceNum;

	private Rect[] rects;
	private int[] orients;
	private SingleFaceInfo[] faces;

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("faceRect", "faceOrient", "faceNum");
	}

	public Rect[] getFaceRects() {
		if (rects == null) {
			parseData();
		}
		return rects;
	}

	private void parseData() {
		rects = new Rect[faceNum];
		orients = new int[faceNum];
		if (faceNum > 0) {
			for (int i = 0; i < faceNum; i++) {
				rects[i] = new Rect(new Pointer(Pointer.nativeValue(faceRect.getPointer()) + faceRect.size() * i));
				orients[i] = faceOrient.getPointer().getInt(i * 4);
			}
		}
	}

	public int[] getFaceOrients() {
		if (orients == null) {
			parseData();
		}
		return orients;
	}

	/**
	 * 辅助方法，方便查看是否有人脸
	 * 
	 * @return true表示有，false无
	 */
	public boolean haveFace() {
		return faceNum <= 0 ? false : true;
	}

	/**
	 * 辅助方法，将多人脸转换为单人脸对象
	 * 
	 * @return 单人脸对象数组，可能为null
	 */
	public SingleFaceInfo[] getFaces() {
		if (faces == null) {
			faces = new SingleFaceInfo[faceNum];
			for (int i = 0; i < faces.length; i++) {
				SingleFaceInfo face = new SingleFaceInfo();
				face.faceOrient = getFaceOrients()[i];
				face.faceRect = getFaceRects()[i];
				faces[i] = face;
			}
		}
		return faces;
	}

}
