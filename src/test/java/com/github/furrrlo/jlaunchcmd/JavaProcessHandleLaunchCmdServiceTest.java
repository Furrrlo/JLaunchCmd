package com.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

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
}