package com.arcsoft.face.bean;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class Rect extends Structure {
    public static class ByValue extends Rect implements Structure.ByValue {
    }
    
    public static class ByReference extends Rect implements Structure.ByReference{
    };
    
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
    
    @SuppressWarnings("rawtypes")
	@Override
    protected List getFieldOrder() { 
        return Arrays.asList("left", "top", "right", "bottom");
    }
    
    @Override
    public String toString() {
    	return "left:"+left+",right:"+right+",top:"+top+",bottom:"+bottom;
    }
}