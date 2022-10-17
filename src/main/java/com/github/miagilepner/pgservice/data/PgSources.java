package com.github.miagilepner.pgservice.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Collector;


@Data
@NoArgsConstructor
public class PgSources {
    private final List<PgSource> sources = new ArrayList<>();

    public PgSources(List<PgSource> sources) {
        this.sources.addAll(sources);
    }

    public PgSources(PgSource... sources) {
        this(Arrays.asList(sources));
    }

    private void add(PgSource source) {
       this.sources.add(source);
    }
    private void addAll(PgSources sources) {
        this.sources.addAll(sources.getSources());
    }
    public Optional<PgSource> getWithName(String name) {
        return sources.stream().filter(e -> e.getName().equals(name)).findFirst();
    }

    public Map<String, PgSource> byHost(){
        return sources.stream().collect(Collectors.toMap(PgSource::getHost, Function.identity()));
    }

    public static Collector<PgSource, ?, PgSources> toPgSources() {
        return Collector.of(PgSources::new, PgSources::add, (left, right) -> {
            left.addAll(right);
            return left;
        });
    }
}
