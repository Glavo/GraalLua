package org.glavo.lua.launcher;

import org.graalvm.collections.Pair;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CmdLineParser {
    public static class CmdResult {
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

    public static Pair<CmdResult, List<String>> parse(List<String> cmd) throws IllegalArgumentException {
        var builder = CmdResult.builder();
        boolean parsingOption = true;
        boolean expectStatement = false;
        boolean expectLibrary = false;
        ArrayList<String> arguments = new ArrayList<>();
        ArrayList<String> libraries = new ArrayList<>();
        ArrayList<String> statements = new ArrayList<>();
        ArrayList<String> unrecognized = new ArrayList<>();
        for (var i : cmd) {
            if (!parsingOption) {
                arguments.add(i);
                continue;
            }
            if (expectStatement) {
                statements.add(i);
                expectStatement = false;
                continue;
            }
            if (expectLibrary) {
                libraries.add(i);
                expectLibrary = false;
                continue;
            }
            switch (i) {
                case "--":
                    parsingOption = false;
                    break;
                case "-E":
                    builder.ignoreEnv = true;
                    break;
                case "-e":
                    expectStatement = true;
                    break;
                case "-i":
                    builder.enterInteractive = true;
                    break;
                case "-l":
                    expectLibrary = true;
                    break;
                case "-v":
                    builder.showVersion = true;
                    break;
                case "-":
                    builder.fromStdin = true;
                    break;
                default:
                    if (i.startsWith("-")) {
                        unrecognized.add(i);
                    } else {
                        builder.scriptPath = Paths.get(i);
                        parsingOption = false;
                    }
            }
        }
        builder.statements = statements.toArray(builder.statements);
        builder.libraries = libraries.toArray(builder.libraries);
        builder.innerArguments = arguments.toArray(builder.innerArguments);
        if (!builder.fromStdin && builder.scriptPath == null) {
            builder.enterInteractive = true;
        }
        return Pair.create(builder.build(), unrecognized);
    }
}
