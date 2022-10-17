package com.github.miagilepner.pgservice.data;

import com.intellij.database.Dbms;
import com.intellij.database.autoconfig.DataSourceRegistry;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.remote.jdbc.helpers.JdbcSettings;
import com.intellij.testFramework.ProjectRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.miagilepner.pgservice.data.TestData.*;
import static org.junit.Assert.*;

public class PgSourceTest  {

    @Rule
    public ProjectRule projectRule = new ProjectRule();

    private LocalDataSource getSourceFromRegistry(DataSourceRegistry registry) {
        Collection<LocalDataSource> sources = registry.getDataSources();
        assertEquals(1, sources.size());
        return sources.stream().findFirst().get();
    }

    @Test
    public void convert_Existing() {
        DataSourceRegistry registry = new DataSourceRegistry(projectRule.getProject());
        registry.getBuilder().withName(NAME).withUrl("oldurl").withUser("olduser").withDbms(Dbms.POSTGRES).commit();
        Map<String, LocalDataSource> existing = registry.getDataSources().stream().collect(Collectors.toMap(LocalDataSource::getName, Function.identity()));
        SOURCE.convert(registry, existing);
        LocalDataSource source = existing.get(NAME);
        assertSource(source);
    }

    @Test
    public void convert_SSLMode() {
        DataSourceRegistry registry = new DataSourceRegistry(projectRule.getProject());
        SOURCE.toBuilder().withSslMode(JdbcSettings.SslMode.VERIFY_FULL).build().convert(registry, new HashMap<>());
        LocalDataSource source = getSourceFromRegistry(registry);
        assertSource(source);
        assertEquals("user-pass", source.getAuthProviderId());
        assertEquals(LocalDataSource.Storage.PERSIST, source.getPasswordStorage());
    }


    @Test
    public void convert_PGPass() {
        DataSourceRegistry registry = new DataSourceRegistry(projectRule.getProject());
        SOURCE.toBuilder().withUsePGPass(true).build().convert(registry, new HashMap<>());
        LocalDataSource source = getSourceFromRegistry(registry);
        assertSource(source);
        assertEquals("pgpass", source.getAuthProviderId());
    }

    @Test
    public void convert_Password() {
        DataSourceRegistry registry = new DataSourceRegistry(projectRule.getProject());
        String password = "my_password";
        SOURCE.toBuilder().withPassword(password).build().convert(registry, new HashMap<>());
        LocalDataSource source = getSourceFromRegistry(registry);
        assertSource(source);
        assertEquals("user-pass", source.getAuthProviderId());
        assertEquals(LocalDataSource.Storage.PERSIST, source.getPasswordStorage());
        assertEquals(password, registry.getCredentialsStore().getCredentialManager().getPassword(source, LocalDataSource.Storage.PERSIST, null).toString(true));
    }


    @Test
    public void authProviderID_PGPass() {
        PgSource source = SOURCE.toBuilder().withUsePGPass(true).build();
        assertEquals("pgpass", source.authProviderID());
    }
    @Test
    public void authProviderID_NoPGPass(){
        PgSource source = SOURCE.toBuilder().withUsePGPass(false).build();
        assertEquals("user-pass", source.authProviderID());
    }

    @Test
    public void buildURL(){
        assertEquals(SOURCE.buildURL(), EXPECTED_URL);
    }
    @Test
    public void updateExistingSource() {
        LocalDataSource source = LocalDataSource.create(NAME, "postgres","jdbc:postgresql://localhost:2345/db", "user");
        SOURCE.updateExistingSource(source);
        assertEquals(EXPECTED_URL, source.getUrl());
        assertEquals(USERNAME, source.getUsername());
        assertEquals("user-pass", source.getAuthProviderId());
    }
    @Test
    public void updateExistingSource_PGPass() {
        LocalDataSource source = LocalDataSource.create(NAME, "postgres","jdbc:postgresql://localhost:2345/db", "user");
        SOURCE.toBuilder().withUsePGPass(true).build().updateExistingSource(source);
        assertEquals("pgpass", source.getAuthProviderId());
    }
    @Test
    public void updateExistingSource_SSLMode() {
        LocalDataSource source = LocalDataSource.create(NAME, "postgres","jdbc:postgresql://localhost:2345/db", "user");
        SOURCE.toBuilder().withSslMode(JdbcSettings.SslMode.VERIFY_CA).build().updateExistingSource(source);
        assertTrue(source.getSslCfg().myEnabled);
        assertEquals(JdbcSettings.SslMode.VERIFY_CA, source.getSslCfg().myMode);
    }
}