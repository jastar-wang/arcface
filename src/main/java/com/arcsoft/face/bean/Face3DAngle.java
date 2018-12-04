package com.arcsoft.face.bean;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

public class Face3DAngle extends Structure {

	public PointerByReference roll;// 横滚角float[]
	public PointerByReference yaw;// 偏航角
	public PointerByReference pitch;// 俯仰角
	public PointerByReference status;// 0: 正常，其他数值:出错 int[]
	public int num;// 检测的人脸个数

	private float[] rolls;
	private float[] yaws;
	private float[] pitchs;
	private int[] statuses;

	private boolean inited = false;

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("roll", "yaw", "pitch", "status", "num");
	}

	public float[] getRolls() {
		initData();
		return rolls;
	}

	public float[] getYaws() {
		initData();
		return yaws;
	}

	public float[] getPitchs() {
		initData();
		return pitchs;
	}

	public int[] getStatuses() {
		initData();
		return statuses;
	}

	private void initData() {
		if (!inited) {
			rolls = new float[num];
			Pointer pointer = roll.getPointer();
			for (int i = 0; i < rolls.length; i++) {
				rolls[i] = pointer.getFloat(4 * i);// 浮点型，4字节
			}

			yaws = new float[num];
			pointer = yaw.getPointer();
			for (int i = 0; i < yaws.length; i++) {
				yaws[i] = pointer.getFloat(4 * i);// 浮点型，4字节
			}

			pitchs = new float[num];
			pointer = pitch.getPointer();
			for (int i = 0; i < pitchs.length; i++) {
				pitchs[i] = pointer.getFloat(4 * i);// 浮点型，4字节
			}

			statuses = new int[num];
			pointer = status.getPointer();
			for (int i = 0; i < statuses.length; i++) {
				statuses[i] = pointer.getInt(4 * i);// 整型，4字节
			}
		}
	}

}
