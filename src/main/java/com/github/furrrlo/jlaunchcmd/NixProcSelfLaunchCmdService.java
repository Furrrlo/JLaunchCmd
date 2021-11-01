package com.github.furrrlo.jlaunchcmd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class NixProcSelfLaunchCmdService implements JLaunchCmdService {

    static class Provider implements JLaunchCmdService.Provider {

        @Override
        public boolean isSupported() {
            // Too many *nix platforms, I'm lazy
            // Just try on anything that isn't windows
            return !System.getProperty("os.name").toLowerCase().contains("win");
        }

        @Override
        public JLaunchCmdService create() {
            return new NixProcSelfLaunchCmdService();
        }
    }

    @Override
    public String[] tryGetLaunchCommand() throws Exception {
        try(final BufferedReader in = new BufferedReader(new FileReader("/proc/self/cmdline"))) {
            return in.readLine().split("\u0000");
        } catch(IOException ex) {
            throw new IOException("/proc/self/cmdline not available", ex);
        }
    }
}
