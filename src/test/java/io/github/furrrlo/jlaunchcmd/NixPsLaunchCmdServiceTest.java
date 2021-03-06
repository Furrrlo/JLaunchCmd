package io.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class NixPsLaunchCmdServiceTest {

    @Test
    @EnabledOnOs({ OS.MAC, OS.LINUX })
    void works() {
        assertDoesNotThrow(() -> new NixPsLaunchCmdService().tryGetLaunchCommand());
    }

    @Test
    @EnabledOnOs({ OS.MAC, OS.LINUX })
    @Disabled("ps output cannot be split properly") // TODO: enabled if this is fixed
    void sameAsJavaProcessHandle() throws Exception {
        final String[] expected, actual;
        try {
            expected = new JavaProcessHandleLaunchCmdService().tryGetLaunchCommand();
        } catch (Throwable t) {
            assumeTrue(false, "Failed to get commandLine from Java ProcessHandle API");
            return;
        }

        assertArrayEquals(
                expected,
                actual = new NixPsLaunchCmdService().tryGetLaunchCommand(),
                String.format("Command line differs (\nexpected:\t%s, \nactual:\t\t%s\n)", Arrays.toString(expected), Arrays.toString(actual)));
    }
}