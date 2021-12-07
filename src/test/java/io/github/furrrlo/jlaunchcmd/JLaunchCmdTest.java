package io.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class JLaunchCmdTest {

    @BeforeAll
    static void beforeAll() {
        System.setProperty("jlaunchcmd.useHandCraftedFallback", "false");
    }

    @Test
    void getLaunchCommand() {
        System.out.println(Arrays.toString(
                Assertions.assertDoesNotThrow(() -> JLaunchCmd.create().getLaunchCommand())));
    }

    @Test
    void getExecutable() {
        System.out.println(Assertions.assertDoesNotThrow(() -> JLaunchCmd.create().getExecutable()));
    }

    @Test
    void getArguments() {
        System.out.println(Arrays.toString(Assertions.assertDoesNotThrow(() -> JLaunchCmd.create().getArguments())));
    }
}