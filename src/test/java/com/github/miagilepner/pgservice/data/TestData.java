package com.github.miagilepner.pgservice.data;

import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.remote.jdbc.helpers.JdbcSettings;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ResourceUtil;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class TestData {
    protected static String NAME = "my_name";
    protected static String USERNAME = "my_username";

    protected static PgSource SOURCE = PgSource.builder()
            .withHost("0.0.0.0")
            .withPort("5432")
            .withDb("my_db")
            .withUsername(USERNAME)
            .withName(NAME)
            .build();

    protected static String EXPECTED_URL = "jdbc:postgresql://0.0.0.0:5432/my_db";

    public static void assertSource(LocalDataSource source) {
        assertEquals(NAME, source.getName());
        assertEquals(EXPECTED_URL, source.getUrl());
        assertEquals(USERNAME, source.getUsername());
        assertEquals("PostgreSQL", source.getDatabaseDriver().getName());
    }

    public static PgSources PG_PASS_SOURCES = new PgSources(
             PgSource.builder()
                    .withName("service1")
                    .withSslMode(JdbcSettings.SslMode.VERIFY_FULL)
                    .withPassword("abc")
                    .withPort("5432")
                    .withUsername("mia")
                    .withHost("192.168.0.1")
                    .withDb("dogs")
                    .withUsePGPass(true)
                    .build(),
            PgSource.builder()
                    .withName("service2")
                    .withHost("example.com")
                    .withHostAddr("127.0.0.1")
                    .withPassword("secret")
                    .withPort("54321")
                    .withUsername("miami")
                    .withDb("frogs")
                    .withUsePGPass(true)
                    .build(),
            PgSource.builder()
                    .withName("service3")
                    .withPort("5432")
                    .withHost("localhost")
                    .withUsername("postgres")
                    .withDb("postgres")
                    .withPassword("asdf")
                    .withUsePGPass(true)
                    .build()
    );
    public static PgSources PG_SERVICE_SOURCES = new PgSources(
            PgSource.builder()
                    .withName("service1")
                    .withSslMode(JdbcSettings.SslMode.VERIFY_FULL)
                    .withPassword("abc")
                    .withPort("5432")
                    .withUsername("mia")
                    .withHost("192.168.0.1")
                    .withDb("dogs")
                    .build(),
            PgSource.builder()
                    .withName("service2")
                    .withHost("example.com")
                    .withHostAddr("127.0.0.1")
                    .withPort("5432")
                    .withUsername("mimi")
                    .withDb("cats")
                    .build(),
            PgSource.builder()
                    .withName("service3")
                    .withPort("5432")
                    .withHost("localhost")
                    .withUsername("postgres")
                    .withDb("postgres")
                    .build()
    );

    public static Profile.Section getPGServiceSection(String name) throws IOException {
        Ini ini = new Ini(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("pg_service.conf")));
        return ini.values().stream().filter(e -> e.getName().equals(name)).findFirst().get();
    }

    public static VirtualFile getPGPassFile() {
        URL u = ResourceUtil.getResource(Thread.currentThread().getContextClassLoader(), ".", "pgpass");
        return VfsUtil.findFileByURL(u);
    }

}