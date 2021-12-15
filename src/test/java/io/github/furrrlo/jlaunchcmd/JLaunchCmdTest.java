package io.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void getExecutablePath() {
        final Path path = Assertions.assertDoesNotThrow(() -> JLaunchCmd.create().getExecutablePath());
        System.out.println(path.toString());
    }

    @Test
    void isExecutablePathAbsolute() throws IOException, InterruptedException, TimeoutException {
        final String res = RunTestBinary.runWithRelativeExecutablePath(ExecutablePathMain.class.getName());
        assertTrue(assertDoesNotThrow(
                () -> Paths.get(res),
                res + " is not a path"
        ).isAbsolute(), res + " is not absolute");
    }

    @Test
    void getArguments() {
        System.out.println(Arrays.toString(Assertions.assertDoesNotThrow(() -> JLaunchCmd.create().getArguments())));
    }
}