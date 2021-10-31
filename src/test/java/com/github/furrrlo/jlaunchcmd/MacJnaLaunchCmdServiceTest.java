package com.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.util.Arrays;

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

        assertDoesNotThrow(service::tryGetLaunchCommand, "Service throws exception");
    }

    @Test
    @EnabledOnOs({ OS.MAC })
    void sameAsJavaProcessHandle() {
        final String[] expected, actual;
        try {
            expected = new JavaProcessHandleLaunchCmdService().tryGetLaunchCommand();
        } catch (Throwable t) {
            assumeTrue(false, "Failed to get commandLine from Java ProcessHandle API");
            return;
        }

        try {
            actual = new MacJnaLaunchCmdService().tryGetLaunchCommand();
        } catch (Throwable t) {
            assumeTrue(false, "Failed to get commandLine from MacOs sysctl");
            return;
        }

        assertArrayEquals(
                expected,
                actual,
                String.format("Command line differs (\nexpected:\t%s, \nactual:\t\t%s\n)", Arrays.toString(expected), Arrays.toString(actual)));
    }
}