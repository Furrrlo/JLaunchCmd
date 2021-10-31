package com.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class WinWmicLaunchCmdServiceTest {

    @Test
    @EnabledOnOs({ OS.WINDOWS })
    void sameAsWin32() throws Exception {
        final String[] expected, actual;
        try {
            expected = new WinJnaLaunchCmdService().tryGetLaunchCommand();
        } catch (Throwable t) {
            Assumptions.assumeTrue(false, "Failed to get commandLine from Win32");
            return;
        }

        assertArrayEquals(
                expected,
                actual = new WinWmicLaunchCmdService().tryGetLaunchCommand(),
                String.format("Command line differs (expected:\t%s, actual:\t\t%s)", Arrays.toString(expected), Arrays.toString(actual)));
    }
}