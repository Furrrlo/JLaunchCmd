package io.github.furrrlo.jlaunchcmd;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface JLaunchCmdService {

    String[] tryGetLaunchCommand() throws Exception;

    default String tryGetShellLaunchCommand() throws Exception {
        return String.join(" ", tryGetLaunchCommand());
    }

    default String tryGetExecutable() throws Exception {
        return tryGetLaunchCommand()[0];
    }

    default Path tryGetExecutablePath() throws Exception {
        final Path executable = Paths.get(tryGetExecutable());
        if(!executable.isAbsolute())
            throw new IOException("Failed to get executable absolute path: " + executable);
        return executable;
    }

    default String[] tryGetArguments() throws Exception {
        final String[] cmd = tryGetLaunchCommand();
        final String[] args = new String[cmd.length - 1];
        System.arraycopy(cmd, 1, args, 0, args.length);
        return args;
    }

    interface Provider {

        boolean isSupported();

        JLaunchCmdService create();
    }
}
