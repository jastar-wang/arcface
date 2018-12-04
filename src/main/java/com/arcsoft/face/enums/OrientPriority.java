package com.arcsoft.face.enums;

/**
 * 定义脸部检测角度的优先级
 *
 * @author Jastar Wang
 * @version 1.0
 * @date 2018/12/4
 */
public interface OrientPriority {
    int AFD_FSDK_OPF_0_ONLY = 0x1; // 检测 0 度方向
    int AFD_FSDK_OPF_90_ONLY = 0x2; // 检测 90 度方向
    int AFD_FSDK_OPF_270_ONLY = 0x3; // 检测 270 度方向
    int AFD_FSDK_OPF_180_ONLY = 0x4; // 检测 180 度方向
    int AFD_FSDK_OPF_0_HIGHER_EXT = 0x5; // 检测 0， 90， 180， 270 四个方向,0 度更优先
}
