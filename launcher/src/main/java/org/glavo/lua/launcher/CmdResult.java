package org.glavo.lua.launcher;

import java.nio.file.Path;

public class CmdResult {
    public final boolean ignoreEnv;
    public final boolean enterInterative;
    public final boolean showVersion;
    public final boolean fromStdin;
    public final Path scriptPath;
    public final String[] innerArguments;
    public final String[] libraries;
    public final String[] statements;

    static class CmdResultBuilder {
        boolean ignoreEnv = false;
        String[] statements = {};
        boolean enterInteractive = false;
        boolean showVersion = false;
        boolean fromStdin = false;
        Path scriptPath = null;
        String[] innerArguments = {};
        String[] libraries = {};

        CmdResult build() {
            return new CmdResult(
                    ignoreEnv,
                    statements,
                    enterInteractive,
                    showVersion,
                    fromStdin,
                    scriptPath,
                    innerArguments,
                    libraries);
        }
    }

    private CmdResult(boolean ignoreEnv,
                      String[] statements,
                      boolean enterInterative,
                      boolean showVersion,
                      boolean fromStdin,
                      Path scriptPath,
                      String[] innerArguments, String[] libraries) {
        this.ignoreEnv = ignoreEnv;
        this.statements = statements;
        this.enterInterative = enterInterative;
        this.showVersion = showVersion;
        this.fromStdin = fromStdin;
        this.scriptPath = scriptPath;
        this.innerArguments = innerArguments;
        this.libraries = libraries;
    }

    public static CmdResultBuilder builder() {
        return new CmdResultBuilder();
    }
}
