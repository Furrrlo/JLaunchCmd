package com.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.*;

class WinJnaLaunchCmdServiceTest {

    @Test
    @EnabledOnOs({ OS.WINDOWS })
    void jnaWorking() {
        final WinJnaLaunchCmdService.Provider provider = new WinJnaLaunchCmdService.Provider();
        assertTrue(provider.isSupported(), "Service is not supported");

        final JLaunchCmdService service = provider.create();
        assertNotNull(service, "Service is null");

        assertDoesNotThrow(service::tryGetLaunchCommand, "Service throws exception");
    }
}