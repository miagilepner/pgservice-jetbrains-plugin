package com.github.miagilepner.pgservice.data;

import com.intellij.database.autoconfig.DataSourceRegistry;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.remote.jdbc.helpers.JdbcSettings;
import com.intellij.openapi.vfs.VirtualFile;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PgSources {
    public static void create(DataSourceRegistry registry, VirtualFile pgService, VirtualFile pgPass) throws IOException {
        Ini ini = new Ini(pgService.getInputStream());
        Collection<Profile.Section> sections = ini.values();
        Map<String, PgSource> databases = sections.stream().map(e -> parseSection(
                e, pgPass != null
        )).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        databases = updateWithPGPass(databases, pgPass);
        Map<String, LocalDataSource> existingSources = registry.getDataSources().stream().collect(Collectors.toMap(LocalDataSource::getName, Function.identity()));
        databases.values().forEach(e -> e.convert(registry, existingSources));
    }

   private static Map.Entry<String, PgSource> parseSection(Profile.Section section, boolean usePGPass) {
        String host = section.get("host", PgServiceBundle.message("pg.default.host"));
        String hostAddr = section.get("hostaddr");
        String hostToUse = host;
        if (hostAddr != null && !hostAddr.isEmpty()) {
            hostToUse = hostAddr;
        }

        PgSource.PgSourceBuilder builder = PgSource.builder()
                .withHost(hostToUse)
                .withName(section.getName())
                .withUsePGPass(usePGPass)
                .withPort(section.getOrDefault("port", PgServiceBundle.message("pg.default.port")))
                .withDb(section.getOrDefault("dbname", PgServiceBundle.message("pg.default.db")))
                .withPassword(section.get("password"))
                .withUsername(section.getOrDefault("user", PgServiceBundle.message("pg.default.username")));
        if(section.containsKey("sslmode")) {
            JdbcSettings.SslMode mode = JdbcSettings.SslMode.valueOf(section.get("sslmode"));
            builder.withSslMode(mode);
        }
        return Map.entry(host, builder.build());
    }

    public static Map<String, PgSource> updateWithPGPass(Map<String, PgSource> databases, VirtualFile pgPass) throws IOException {
        if (pgPass == null) {
            return databases;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(pgPass.getInputStream()));
        String line;
        Pattern pattern = Pattern.compile("(?<host>.*):(?<port>.*):(?<db>.*):(?<user>.*):(?<pass>.*)");
        while ((line = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                if (databases.containsKey(matcher.group("host"))) {
                    String host = matcher.group("host");
                    PgSource database = databases.get(host);
                    String port = matcher.group("port");
                    String db = matcher.group("db");
                    String user = matcher.group("user");
                    String pass = matcher.group("pass");
                    if (!port.equals("*")) {
                        database.setPort(port);
                    }
                    if (!db.equals("*")) {
                        database.setDb(db);
                    }
                    if (!user.equals("*")) {
                        database.setUsername(user);
                    }
                    if (!pass.equals("*")) {
                        database.setPassword(pass);
                    }
                    databases.put(host, database);
                }
            }
        }
        return databases;
    }
}
