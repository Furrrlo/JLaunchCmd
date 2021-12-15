package io.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class WinJnaLaunchCmdServiceTest {

    @Test
    @EnabledOnOs({ OS.WINDOWS })
    void jnaWorking() {
        assumeTrue(Boolean.getBoolean("junit.jna"), "Missing JNA");

        final WinJnaLaunchCmdService.Provider provider = new WinJnaLaunchCmdService.Provider();
        assertTrue(provider.isSupported(), "Service is not supported");

        final JLaunchCmdService service = provider.create();
        assertNotNull(service, "Service is null");

        assertAll(
                () -> assertDoesNotThrow(service::tryGetLaunchCommand, "Service#tryGetLaunchCommand throws exception"),
                () -> assertTrue(assertDoesNotThrow(
                                service::tryGetExecutablePath,
                                "Service#tryGetExecutablePath throws exception").isAbsolute(),
                        "tryGetExecutablePath is not absolute"));
    }

    @Test
    @EnabledOnOs({ OS.WINDOWS })
    void isExecutablePathAbsolute() throws IOException, InterruptedException, TimeoutException {
        assumeTrue(Boolean.getBoolean("junit.jna"), "Missing JNA");

        final String res = RunTestBinary.runWithRelativeExecutablePath(
                ExecutablePathMain.class.getName(),
                WinJnaLaunchCmdService.Provider.class.getName());
        assertTrue(assertDoesNotThrow(
                () -> Paths.get(res),
                res + " is not a path"
        ).isAbsolute(), res + " is not absolute");
    }
}