package com.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.*;

class MacSysctlLaunchCommandServiceTest {

    @Test
    @EnabledOnOs(OS.MAC)
    void works() {
        assertDoesNotThrow(() -> new MacSysctlLaunchCommandService().tryGetLaunchCommand());
    }
}