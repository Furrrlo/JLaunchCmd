package io.github.furrrlo.jlaunchcmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

class WinWmicLaunchCmdService implements JLaunchCmdService {

    static class Provider implements JLaunchCmdService.Provider {

        @Override
        public boolean isSupported() {
            return System.getProperty("os.name").toLowerCase().contains("win");
        }

        @Override
        public JLaunchCmdService create() {
            return new WinWmicLaunchCmdService();
        }
    }

    private final PidProvider pidProvider;
    private final CommandLineSplitter commandLineSplitter;

    public WinWmicLaunchCmdService() {
        this(PidProvider.INSTANCE, CommandLineSplitter.INSTANCE);
    }

    public WinWmicLaunchCmdService(PidProvider pidProvider, CommandLineSplitter commandLineSplitter) {
        this.pidProvider = pidProvider;
        this.commandLineSplitter = commandLineSplitter;
    }

    @Override
    public String[] tryGetLaunchCommand() throws Exception {
        return commandLineSplitter.splitCommand(tryGetShellLaunchCommand());
    }

    @Override
    public String tryGetShellLaunchCommand() throws Exception {
        return queryWmicParam(pidProvider.getPid(), "CommandLine");
    }

    @Override
    public Path tryGetExecutablePath() throws Exception {
        return Paths.get(queryWmicParam(pidProvider.getPid(), "ExecutablePath"));
    }

    private String queryWmicParam(long pid, String paramName) throws Exception {
        final String output;
        try {
            // See https://stackoverflow.com/a/14143925
            final String cmd = "wmic.exe PROCESS where processid=" + pid + " get " + paramName;
            final Process process = Runtime.getRuntime().exec(cmd);

            output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines()
                    .collect(Collectors.joining("\n"))
                    .trim();
            if(!process.waitFor(1, TimeUnit.SECONDS))
                throw new TimeoutException("Process waitFor time has elapsed");

            if(!output.toLowerCase().startsWith(paramName.toLowerCase()))
                throw new RuntimeException("Invalid wmic.exe output");
        } catch(IOException ex) {
            throw new IOException("wmic.exe not available", ex);
        }

        return output.substring(paramName.length()).trim();
    }
}
