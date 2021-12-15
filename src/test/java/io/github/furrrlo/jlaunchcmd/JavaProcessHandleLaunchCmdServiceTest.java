package io.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

class JavaProcessHandleLaunchCmdServiceTest {

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void reflectionsWorking() {
        try {
            final JavaProcessHandleLaunchCmdService.Provider provider = new JavaProcessHandleLaunchCmdService.Provider();
            assertTrue(provider.isSupported(), "Reflections failed (service is not supported)");

            final JLaunchCmdService service = provider.create();
            assertNotNull(service, "Reflections failed (service is supported, but null)");

            service.tryGetLaunchCommand();
        } catch (ReflectiveOperationException ex) {
            fail("Reflections failed", ex);
        } catch (Throwable ex) {
            // ignored, don't care if it fails, just that reflections work
        }
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void isExecutablePathAbsolute() throws IOException, InterruptedException, TimeoutException {
        final String res = RunTestBinary.runWithRelativeExecutablePath(
                ExecutablePathMain.class.getName(),
                JavaProcessHandleLaunchCmdService.Provider.class.getName());
        assertTrue(assertDoesNotThrow(
                () -> Paths.get(res),
                res + " is not a path"
        ).isAbsolute(), res + " is not absolute");
    }
}