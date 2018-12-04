package com.arcsoft.face.bean;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class FRVersion extends Structure {
    public int lCodebase;
    public int lMajor;
    public int lMinor;
    public int lBuild;
    public int lFeatureLevel;
    public String Version;
    public String BuildDate;
    public String CopyRight;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[]{"lCodebase", "lMajor", "lMinor", "lBuild", "lFeatureLevel", "Version",
                "BuildDate", "CopyRight"});
    }
}
