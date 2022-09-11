package com.github.miagilepner.pgservice.data;

import com.intellij.database.Dbms;
import com.intellij.database.autoconfig.DataSourceDetector;
import com.intellij.database.autoconfig.DataSourceRegistry;
import com.intellij.database.dataSource.DataSourceSslConfiguration;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.remote.jdbc.helpers.JdbcSettings;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;

import java.util.Map;
import java.util.Optional;

@Builder(setterPrefix = "with")
@Setter
public class PgSource {
    @NonNull String host;
    @NonNull String name;
    @NonNull String port;
    @NonNull String username;
    @NonNull String db;
    String password;
    boolean usePGPass;
    JdbcSettings.SslMode sslMode;

    private void updateSSLPGPass(LocalDataSource source) {
        if (!usePGPass) {
            source.setPasswordStorage(LocalDataSource.Storage.PERSIST);
        }
        if (sslMode != null) {
            source.setSslCfg(new DataSourceSslConfiguration("", "", "", true, sslMode));
        }

    }
    private String buildURL() {
        return String.format("jdbc:postgresql://%s:%s/%s", host, port, db);
    }

    private String authProviderID() {
        if (usePGPass) {
            return "pgpass";
        }
        return "user-pass";
    }

    private void updateExistingSource(LocalDataSource source) {
        source.setUrl(buildURL());
        source.setUsername(username);
        source.setDbms(Dbms.POSTGRES);
        source.setAuthProviderId(authProviderID());
        updateSSLPGPass(source);
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
            updateSSLPGPass(sourceValue.get());
        }
    }
}
