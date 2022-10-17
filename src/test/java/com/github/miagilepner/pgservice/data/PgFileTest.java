package com.github.miagilepner.pgservice.data;

import org.junit.Test;

import static com.intellij.openapi.util.io.IoTestUtil.assumeUnix;
import static com.intellij.openapi.util.io.IoTestUtil.assumeWindows;
import static org.junit.Assert.*;

public class PgFileTest {
    static final String HOME = System.getProperty("user.home");

    @Test
    public void testDefaultPGServiceWindows() {
        assumeWindows();
        String file = PgFile.defaultPGServiceFile();
        assertEquals( HOME+"\\postgresql\\.pg_service.conf", file);
    }
    @Test
    public void testDefaultPGPassWindows() {
        assumeWindows();
        String file = PgFile.defaultPGPassFile();
        assertEquals( HOME+"\\postgresql\\.pgpass", file);
    }
    @Test
    public void testDefaultPGServiceUnix() {
        assumeUnix();
        String file = PgFile.defaultPGServiceFile();
        assertEquals(file, HOME+"/.pg_service.conf", file);
    }
    @Test
    public void testDefaultPGPassUnix() {
        assumeUnix();
        String file = PgFile.defaultPGPassFile();
        assertEquals(file, HOME+"/.pgpass", file);
    }
}