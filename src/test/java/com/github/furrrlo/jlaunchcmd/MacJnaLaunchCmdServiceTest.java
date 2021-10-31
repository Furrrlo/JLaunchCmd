package com.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MacJnaLaunchCmdServiceTest {

    @Test
    @EnabledOnOs({ OS.MAC })
    void jnaWorking() {
        assumeTrue(Boolean.getBoolean("junit.jna"), "Missing JNA");

        final MacJnaLaunchCmdService.Provider provider = new MacJnaLaunchCmdService.Provider();
        assertTrue(provider.isSupported(), "Service is not supported");

        final JLaunchCmdService service = provider.create();
        assertNotNull(service, "Service is null");

        assertDoesNotThrow(service::tryGetLaunchCommand, "Service throws exception");
    }

    @Test
    @EnabledOnOs({ OS.MAC })
    void sameAsJavaProcessHandle() {
        final String[] expected, actual;
        try {
            expected = new JavaProcessHandleLaunchCmdService().tryGetLaunchCommand();
            actual = new MacJnaLaunchCmdService().tryGetLaunchCommand();
        } catch (Throwable t) {
            Assumptions.assumeTrue(false, "Failed to get commandLine from Win32");
            return;
        }

        assertArrayEquals(
                expected,
                actual,
                String.format("Command line differs (expected:\t%s, actual:\t\t%s)", Arrays.toString(expected), Arrays.toString(actual)));
    }
}