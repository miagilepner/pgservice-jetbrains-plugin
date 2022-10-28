package com.github.miagilepner.pgservice.data;

import com.github.miagilepner.pgservice.dialogs.ImportProgressIndicator;
import com.intellij.database.Dbms;
import com.intellij.database.autoconfig.DataSourceDetector;
import com.intellij.database.autoconfig.DataSourceRegistry;
import com.intellij.database.dataSource.DataSourceSslConfiguration;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.remote.jdbc.helpers.JdbcSettings;
import com.intellij.openapi.util.text.Strings;
import lombok.*;
import org.ini4j.Profile;

import java.util.Map;
import java.util.Optional;

@Builder(setterPrefix = "with", toBuilder = true)
@Data
public class PgSource {
    @NonNull String host;
    String hostAddr;
    @NonNull String name;
    @NonNull String port;
    @NonNull String username;
    @NonNull String db;
    String password;
    boolean usePGPass;
    JdbcSettings.SslMode sslMode;

    protected String buildURL() {
        String hostToUse = host;
        if (hostAddr != null && !Strings.isEmpty(hostAddr)) {
            hostToUse = hostAddr;
        }
        return String.format("jdbc:postgresql://%s:%s/%s", hostToUse, port, db);
    }

    protected String authProviderID() {
        if (usePGPass) {
            return "pgpass";
        }
        return "user-pass";
    }

    protected void updateExistingSource(LocalDataSource source) {
        source.setUrl(buildURL());
        source.setUsername(username);
        source.setDbms(Dbms.POSTGRES);
        source.setAuthProviderId(authProviderID());
        if (sslMode != null) {
            source.setSslCfg(new DataSourceSslConfiguration("", "", "", true, sslMode));
        }
    }

    public void convert(DataSourceRegistry registry, Map<String, LocalDataSource> existingSources) {
        if (existingSources.containsKey(name)) {
            updateExistingSource(existingSources.get(name));
            return;
        }
        DataSourceDetector.Builder builder = registry.getBuilder().withDriver("postgres")
                .withDbms(Dbms.POSTGRES)
                .withUser(username)
                .withName(name)
                .withUrl(buildURL());
        if (password != null && !password.isEmpty()) {
            builder = builder.withPassword(password);
        }
        builder.withAuthProviderId(authProviderID()).commit();
        Optional<? extends LocalDataSource> sourceValue = registry.getNewDataSources().stream().filter(e -> e.getName().equals(name)).findFirst();
        if (sourceValue.isPresent()) {
            LocalDataSource source = sourceValue.get();
            if (!usePGPass) {
                source.setPasswordStorage(LocalDataSource.Storage.PERSIST);
            }
            if (sslMode != null) {
                source.setSslCfg(new DataSourceSslConfiguration("", "", "", true, sslMode));
            }
        }
    }

    public static PgSource fromSection(Profile.Section section, boolean usePGPass, ImportProgressIndicator indicator) {
        if (indicator != null) {
            indicator.addSection();
        }
        String host = section.get("host", PgServiceBundle.message("pg.default.host"));

        PgSource.PgSourceBuilder builder = PgSource.builder()
                .withHost(host)
                .withHostAddr(section.get("hostaddr"))
                .withName(section.getName())
                .withUsePGPass(usePGPass)
                .withPort(section.getOrDefault("port", PgServiceBundle.message("pg.default.port")))
                .withDb(section.getOrDefault("dbname", PgServiceBundle.message("pg.default.db")))
                .withPassword(section.get("password"))
                .withUsername(section.getOrDefault("user", PgServiceBundle.message("pg.default.username")));
        if (section.containsKey("sslmode")) {
            JdbcSettings.SslMode mode = JdbcSettings.SslMode.valueOf(section.get("sslmode").toUpperCase().replaceAll("-", "_"));
            builder.withSslMode(mode);
        }
        return builder.build();

    }
}
