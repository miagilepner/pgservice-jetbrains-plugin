package com.github.miagilepner.pgservice.data;

import com.github.miagilepner.pgservice.dialogs.ImportProgressIndicator;
import com.intellij.database.autoconfig.DataSourceRegistry;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.openapi.vfs.VirtualFile;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.miagilepner.pgservice.data.PgSource.fromSection;

public class Import {
    public static void create(DataSourceRegistry registry, VirtualFile pgService, VirtualFile pgPass, ImportProgressIndicator indicator) throws IOException {
        Ini ini = new Ini(pgService.getInputStream());
        Collection<Profile.Section> sections = ini.values();
        indicator.setNumSections(sections.size());

        // Get sources from the pg_service file
        PgSources sources = sections.stream().map(e -> fromSection(
                e, pgPass != null, indicator
        )).collect(PgSources.toPgSources());

        // Update the sources with the pgpass file
        sources = PgPass.updateWithPGPass(sources, pgPass);

        // Add the sources to the registry, updating existing sources if they match by name
        Map<String, LocalDataSource> existingSources = registry.getDataSources().stream().collect(Collectors.toMap(LocalDataSource::getName, Function.identity()));
        sources.getSources().forEach(e -> e.convert(registry, existingSources));

        indicator.done();
    }

}
