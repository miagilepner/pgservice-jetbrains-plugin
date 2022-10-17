package com.github.miagilepner.pgservice.data;

import com.intellij.openapi.vfs.VirtualFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PgPass {
    protected static Pattern PATTERN = Pattern.compile("(?<host>.*):(?<port>.*):(?<db>.*):(?<user>.*):(?<pass>.*)");

    public static PgSources updateWithPGPass(PgSources sources, VirtualFile pgPass) throws IOException {
        if (pgPass == null) {
            return sources;
        }
        Map<String, PgSource> databases = sources.byHost();
        BufferedReader reader = new BufferedReader(new InputStreamReader(pgPass.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = PATTERN.matcher(line);
            if (matcher.matches()) {
                String host = matcher.group("host");
                if (databases.containsKey(host)) {
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
        return sources;
    }
}
