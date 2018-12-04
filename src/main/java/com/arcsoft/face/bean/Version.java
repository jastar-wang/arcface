package com.arcsoft.face.bean;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

import lombok.ToString;

@ToString
public class Version extends Structure {

	public String Version;
	public String BuildDate;
	public String CopyRight;

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("Version", "BuildDate", "CopyRight");
	}

}
