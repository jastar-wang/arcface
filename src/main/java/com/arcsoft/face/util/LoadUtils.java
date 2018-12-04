package com.arcsoft.face.util;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoadUtils {
    public static <T> T loadOSLibrary(String dirPath, String libname, Class<T> interfaceClass) {
        String filePath = dirPath + "/";
        if (Platform.isWindows()) {
            if (Platform.is64Bit()) {
                filePath += "win/x64/" + "lib" + libname + ".dll";
            } else {
                filePath += "win/win32-x86/" + "lib" + libname + ".dll";
            }
        } else if (Platform.is64Bit() && Platform.isLinux()) {
            filePath += "linux/x64/" + "lib" + libname + ".so";
        } else {
            log.error("unsupported platform");
            System.exit(1);
        }

        return loadLibrary(filePath, interfaceClass);
    }

    public static <T> T loadLibrary(String filePath, Class<T> interfaceClass) {
        return Native.loadLibrary(filePath, interfaceClass);
    }
}
