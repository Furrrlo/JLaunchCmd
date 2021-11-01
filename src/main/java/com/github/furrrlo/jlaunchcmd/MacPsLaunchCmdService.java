package com.github.furrrlo.jlaunchcmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

class MacPsLaunchCmdService implements JLaunchCmdService {

    static class Provider implements JLaunchCmdService.Provider {

        @Override
        public boolean isSupported() {
            final String osName = System.getProperty("os.name").toLowerCase();
            return osName.contains("mac") || osName.contains("darwin");
        }

        @Override
        public JLaunchCmdService create() {
            return new MacPsLaunchCmdService();
        }
    }

    private final PidProvider pidProvider;
    private final CommandLineSplitter commandLineSplitter;

    public MacPsLaunchCmdService() {
        this(PidProvider.INSTANCE, CommandLineSplitter.INSTANCE);
    }

    public MacPsLaunchCmdService(PidProvider pidProvider, CommandLineSplitter commandLineSplitter) {
        this.pidProvider = pidProvider;
        this.commandLineSplitter = commandLineSplitter;
    }

    @Override
    public String[] tryGetLaunchCommand() throws Exception {
        final long pid = pidProvider.getPid();

        final String output;
        try {
            // See https://stackoverflow.com/a/14143925
            final String cmd = "ps -a -o command= -p " + pid;
            final Process process = Runtime.getRuntime().exec(cmd);

            output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines()
                    .collect(Collectors.joining("\n"))
                    .trim();
            if(!process.waitFor(1, TimeUnit.SECONDS))
                throw new TimeoutException("Process waitFor time has elapsed");
        } catch(IOException ex) {
            throw new IOException("ps not available", ex);
        }

        return commandLineSplitter.splitCommand(output);
    }
}
