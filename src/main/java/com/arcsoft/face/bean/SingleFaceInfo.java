package com.arcsoft.face.bean;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class SingleFaceInfo extends Structure {

	public Rect faceRect;
	public int faceOrient;

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("faceRect", "faceOrient");
	}

	/**
	 * 获取XY轴起始坐标及宽度、高度
	 *
	 * @author Jastar Wang
	 * @date 2018/12/3
	 * @version 1.0
	 */
	public int getLeft() {
		return faceRect.left;
	}

	public int getTop() {
		return faceRect.top;
	}

	public int getWidth() {
		return faceRect.right - faceRect.left;
	}

	public int getHeight() {
		return faceRect.bottom - faceRect.top;
	}

}
