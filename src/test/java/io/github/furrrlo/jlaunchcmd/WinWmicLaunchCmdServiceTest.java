package io.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

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

    @Test
    @EnabledOnOs({ OS.WINDOWS })
    void exePathSameAsWin32() throws Exception {
        final Path expected;
        try {
            expected = new WinJnaLaunchCmdService().tryGetExecutablePath();
        } catch (Throwable t) {
            assumeTrue(false, "Failed to get commandLine from Win32");
            return;
        }

        assertEquals(
                expected,
                new WinWmicLaunchCmdService().tryGetExecutablePath(),
                "Different executable path");
    }

    @Test
    @EnabledOnOs({ OS.WINDOWS })
    void isExecutablePathAbsolute() throws IOException, InterruptedException, TimeoutException {
        final String res = RunTestBinary.runWithRelativeExecutablePath(
                ExecutablePathMain.class.getName(),
                WinWmicLaunchCmdService.Provider.class.getName());
        assertTrue(assertDoesNotThrow(
                () -> Paths.get(res),
                res + " is not a path"
        ).isAbsolute(), res + " is not absolute");
    }
}