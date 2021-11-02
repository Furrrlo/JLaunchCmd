package com.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class NixPsLaunchCmdServiceTest {

    @Test
    @EnabledOnOs({ OS.MAC, OS.LINUX })
    void works() {
        assertDoesNotThrow(() -> new NixPsLaunchCmdService().tryGetLaunchCommand());
    }

    @Test
    @EnabledOnOs({ OS.MAC, OS.LINUX })
    @Disabled("ps output cannot be split properly") // TODO: enabled if this is fixed
    void sameAsJavaProcessHandle() throws Exception {
        final String[] expected, actual;
        try {
            expected = new JavaProcessHandleLaunchCmdService().tryGetLaunchCommand();
        } catch (Throwable t) {
            assumeTrue(false, "Failed to get commandLine from Java ProcessHandle API");
            return;
        }

        assertArrayEquals(
                expected,
                actual = new NixPsLaunchCmdService().tryGetLaunchCommand(),
                String.format("Command line differs (\nexpected:\t%s, \nactual:\t\t%s\n)", Arrays.toString(expected), Arrays.toString(actual)));
    }

    @Test
    @EnabledOnOs(OS.MAC)
    void testSysctlCmd() throws Exception {

        final String output;
        try {
            final String cmd = "sysctl -a -o";
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

        System.out.println("sysctl -a\n " + output);
    }

    @Test
    @EnabledOnOs(OS.MAC)
    void testSysctlCmd2() throws Exception {

        final String output;
        try {
            final String cmd = "sysctl kern.procargs2";
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

        System.out.println("sysctl -a\n " + output);
    }
}