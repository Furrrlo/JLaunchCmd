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
                String.format("Command line differs (\nexpected:\t%s, \nactual:\t\t%s\n)", Arrays.toString(expected), Arrays.toString(actual)));
    }

    @Test
    @EnabledOnOs({ OS.LINUX })
    void exePathSameAsJavaProcessHandle() throws Exception {
        final Path expected;
        try {
            expected = new JavaProcessHandleLaunchCmdService().tryGetExecutablePath();
        } catch (Throwable t) {
            assumeTrue(false, "Failed to get commandLine from Java ProcessHandle API");
            return;
        }

        assertEquals(expected, new NixProcSelfLaunchCmdService().tryGetExecutablePath());
    }

    @Test
    @EnabledOnOs({ OS.LINUX })
    void isExecutablePathAbsolute() throws IOException, InterruptedException, TimeoutException {
        final String res = RunTestBinary.runWithRelativeExecutablePath(
                ExecutablePathMain.class.getName(),
                NixProcSelfLaunchCmdService.Provider.class.getName());
        assertTrue(assertDoesNotThrow(
                () -> Paths.get(res),
                res + " is not a path"
        ).isAbsolute(), res + " is not absolute");
    }
}