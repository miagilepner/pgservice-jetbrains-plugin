package com.github.miagilepner.pgservice.data;

import com.intellij.testFramework.ApplicationRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.github.miagilepner.pgservice.data.TestData.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

public class PgPassTest {
    @Rule
    public ApplicationRule applicationRule = new ApplicationRule();
    @Test
    public void updateWithPGPass_Null() throws IOException {
        assertEquals(PG_SERVICE_SOURCES, PgPass.updateWithPGPass(PG_SERVICE_SOURCES, null));
    }

    @Test
    public void updateWithPGPass() throws IOException {
        PgSources sources = PgPass.updateWithPGPass(PG_SERVICE_SOURCES.getSources().stream().map(s -> s.toBuilder().withUsePGPass(true).build()).collect(PgSources.toPgSources()), getPGPassFile());
        assertThat(PG_PASS_SOURCES.getSources(), containsInAnyOrder(sources.getSources().toArray()));
    }
}
