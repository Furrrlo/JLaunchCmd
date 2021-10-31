package com.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JLaunchCmdTest {

    @BeforeAll
    static void beforeAll() {
        System.setProperty("jlaunchcmd.useHandCraftedFallback", "false");
    }

    @Test
    void getLaunchCommand() {
        assertDoesNotThrow(() -> JLaunchCmd.create().getLaunchCommand());
    }

    @Test
    void getExecutable() {
        assertDoesNotThrow(() -> JLaunchCmd.create().getExecutable());
    }

    @Test
    void getArguments() {
        assertDoesNotThrow(() -> JLaunchCmd.create().getArguments());
    }
}