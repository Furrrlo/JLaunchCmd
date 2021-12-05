package com.github.furrrlo.jlaunchcmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        final long pid = pidProvider.getPid();

        final String output;
        try {
            // See https://stackoverflow.com/a/14143925
            final String cmd = "wmic.exe PROCESS where processid=" + pid + " get CommandLine";
            final Process process = Runtime.getRuntime().exec(cmd);

            output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines()
                    .collect(Collectors.joining("\n"))
                    .trim();
            if(!process.waitFor(1, TimeUnit.SECONDS))
                throw new TimeoutException("Process waitFor time has elapsed");

            if(!output.toLowerCase().startsWith("commandline"))
                throw new RuntimeException("Invalid wmic.exe output");
        } catch(IOException ex) {
            throw new IOException("wmic.exe not available", ex);
        }

        return output.substring("CommandLine".length()).trim();
    }
}
