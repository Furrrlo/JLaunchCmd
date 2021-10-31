package com.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class NixProcSelfLaunchCmdServiceTest {

    @Test
    @EnabledOnOs({ OS.LINUX })
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
                actual = new NixProcSelfLaunchCmdService().tryGetLaunchCommand(),
                String.format("Command line differs (expected:\t%s, actual:\t\t%s)", Arrays.toString(expected), Arrays.toString(actual)));
    }

}