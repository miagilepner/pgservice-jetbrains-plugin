package com.github.miagilepner.pgservice.data;

import com.intellij.testFramework.ApplicationRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static com.github.miagilepner.pgservice.data.TestData.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class PgSourcesTest {

    @Rule
    public ApplicationRule applicationRule = new ApplicationRule();

    String serviceName;
    boolean usePGPass;
    public PgSourcesTest(String serviceName, boolean usePGPass) {
        this.serviceName = serviceName;
        this.usePGPass = usePGPass;
    }
    @Parameterized.Parameters
    public static Collection sources() {
        return Arrays.asList(new Object[][] {
                { "service1", true },
                { "service1", false },
                { "service2", true },
                { "service2", false },
                { "service3", true },
                { "service3", false }
        });
    }

    @Test
    public void parseSection() throws IOException {
        PgSource parsed = PgSource.fromSection(TestData.getPGServiceSection(serviceName), usePGPass, null);
        Optional<PgSource> expectedSource = PG_SERVICE_SOURCES.getWithName(serviceName);
        assertTrue(expectedSource.isPresent());
        PgSource source = expectedSource.get();
        if (usePGPass) {
            source = source.toBuilder().withUsePGPass(true).build();
        }
        assertEquals(source, parsed);

    }
}
