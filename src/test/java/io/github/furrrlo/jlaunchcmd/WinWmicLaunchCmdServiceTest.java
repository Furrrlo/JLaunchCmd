package io.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class WinWmicLaunchCmdServiceTest {

    @Test
    @EnabledOnOs({ OS.WINDOWS })
    void sameAsWin32() throws Exception {
        final String[] expected, actual;
        try {
            expected = new WinJnaLaunchCmdService().tryGetLaunchCommand();
        } catch (Throwable t) {
            assumeTrue(false, "Failed to get commandLine from Win32");
            return;
        }

        assertArrayEquals(
                expected,
                actual = new WinWmicLaunchCmdService().tryGetLaunchCommand(),
                String.format("Command line differs (\nexpected:\t%s, \nactual:\t\t%s\n)", Arrays.toString(expected), Arrays.toString(actual)));
    }
}