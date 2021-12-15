package io.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

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
                        "tryGetExecutablePath is not absolute"))
        ;
    }
}