package com.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class JLaunchCmdTest {

    @BeforeAll
    static void beforeAll() {
        System.setProperty("jlaunchcmd.useHandCraftedFallback", "false");
    }

    @Test
    void getLaunchCommand() {
        System.out.println(Arrays.toString(
                assertDoesNotThrow(() -> JLaunchCmd.create().getLaunchCommand())));
    }

    @Test
    void getExecutable() {
        System.out.println(assertDoesNotThrow(() -> JLaunchCmd.create().getExecutable()));
    }

    @Test
    void getArguments() {
        System.out.println(Arrays.toString(assertDoesNotThrow(() -> JLaunchCmd.create().getArguments())));
    }
}