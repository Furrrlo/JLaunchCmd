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

class MacJnaLaunchCmdServiceTest {

    @Test
    @EnabledOnOs({ OS.MAC })
    void jnaWorking() {
        assumeTrue(Boolean.getBoolean("junit.jna"), "Missing JNA");

        final MacJnaLaunchCmdService.Provider provider = new MacJnaLaunchCmdService.Provider();
        assertTrue(provider.isSupported(), "Service is not supported");

        final JLaunchCmdService service = provider.create();
        assertNotNull(service, "Service is null");

        assertAll(
                () -> assertDoesNotThrow(service::tryGetLaunchCommand, "Service#tryGetLaunchCommand throws exception"),
                () -> assertTrue(assertDoesNotThrow(
                                service::tryGetExecutablePath,
                                "Service#tryGetExecutablePath throws exception").isAbsolute(),
                        "tryGetExecutablePath is not absolute")
        );
    }

    @Test
    @EnabledOnOs({ OS.MAC })
    void sameAsJavaProcessHandle() throws Exception {
        assumeTrue(Boolean.getBoolean("junit.jna"), "Missing JNA");

        final String[] expected, actual;
        try {
            expected = new JavaProcessHandleLaunchCmdService().tryGetLaunchCommand();
        } catch (Throwable t) {
            assumeTrue(false, "Failed to get commandLine from Java ProcessHandle API");
            return;
        }

        assertArrayEquals(
                expected,
                actual = new MacJnaLaunchCmdService().tryGetLaunchCommand(),
                String.format("Command line differs (\nexpected:\t%s, \nactual:\t\t%s\n)", Arrays.toString(expected), Arrays.toString(actual)));
    }

    @Test
    @EnabledOnOs({ OS.MAC })
    void exePathSameAsJavaProcessHandle() throws Exception {
        assumeTrue(Boolean.getBoolean("junit.jna"), "Missing JNA");

        final Path expected;
        try {
            expected = new JavaProcessHandleLaunchCmdService().tryGetExecutablePath();
        } catch (Throwable t) {
            assumeTrue(false, "Failed to get commandLine from Java ProcessHandle API");
            return;
        }

        assertEquals(expected, new MacJnaLaunchCmdService().tryGetExecutablePath());
    }

    @Test
    @EnabledOnOs({ OS.MAC })
    void isExecutablePathAbsolute() throws IOException, InterruptedException, TimeoutException {
        assumeTrue(Boolean.getBoolean("junit.jna"), "Missing JNA");

        final String res = RunTestBinary.runWithRelativeExecutablePath(
                ExecutablePathMain.class.getName(),
                MacJnaLaunchCmdService.Provider.class.getName());
        assertTrue(assertDoesNotThrow(
                () -> Paths.get(res),
                res + " is not a path"
        ).isAbsolute(), res + " is not absolute");
    }
}