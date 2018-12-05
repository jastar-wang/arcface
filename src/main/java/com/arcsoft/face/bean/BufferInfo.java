package com.arcsoft.face.bean;

/**
 * 图片缓存信息
 * 
 * @author Jastar·Wang
 * @email jastar_wang@163.com
 * @date 2018-12-05
 * @since 2.0
 */
public class BufferInfo {
	public int width;
	public int height;
	public byte[] buffer;

	public BufferInfo(int w, int h, byte[] buf) {
		width = w;
		height = h;
		buffer = buf;
	}
}