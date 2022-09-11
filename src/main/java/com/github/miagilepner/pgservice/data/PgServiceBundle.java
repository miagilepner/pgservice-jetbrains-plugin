package com.github.miagilepner.pgservice.data;

import com.intellij.DynamicBundle;

public class PgServiceBundle extends DynamicBundle {
    public static final PgServiceBundle INSTANCE = new PgServiceBundle();
    private static final String BUNDLE_PATH = "messages.PGServiceBundle";
    PgServiceBundle() {
        super(BUNDLE_PATH);
    }

    public static String message(String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }
}
