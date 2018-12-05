package com.arcsoft.face.bean;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import lombok.ToString;

/**
 * 人脸位置
 * 
 * @author Jastar·Wang
 * @email jastar_wang@163.com
 * @date 2018-12-05
 * @since 2.0
 */
@ToString
public class Rect extends Structure {
	public static class ByValue extends Rect implements Structure.ByValue {
	}

	public static class ByReference extends Rect implements Structure.ByReference {
	}

	public int left;
	public int top;
	public int right;
	public int bottom;

	public Rect() {
	}

	public Rect(Pointer p) {
		super.useMemory(p);
		read();
	}

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("left", "top", "right", "bottom");
	}

	/**
	 * X轴起点
	 * 
	 * @return
	 */
	public int getLeft() {
		return this.left;
	}

	/**
	 * Y轴起点
	 * 
	 * @return
	 */
	public int getTop() {
		return this.top;
	}

	/**
	 * 宽
	 * 
	 * @return
	 */
	public int getWidth() {
		return this.right - this.left;
	}

	/**
	 * 高
	 * 
	 * @return
	 */
	public int getHeight() {
		return this.bottom - this.top;
	}

}