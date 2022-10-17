package com.github.miagilepner.pgservice.data;

import com.intellij.openapi.util.SystemInfoRt;

public class PgFile {
    public static String defaultPGServiceFile() {
        return defaultFile("pgservice.default.file");
    }
    public static String defaultPGPassFile() {
        return defaultFile("pgpass.default.file");
    }
    private static String defaultFile(String prop) {
        String userHome = System.getProperty("user.home");
        if (SystemInfoRt.isWindows) {
            prop = prop +".windows";
        }
        return PgServiceBundle.message(prop, userHome);
    }
}
