package io.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class JnaPidProviderTest {

    @Test
    void sameAsJavaProcessHandlePid() {
        assumeTrue(Boolean.getBoolean("junit.jna"), "Missing JNA");

        long pid;
        try {
            pid = new PidProviderImpl().getPidWithJavaApi();
        } catch (Throwable t) {
            assumeTrue(false, "PidProviderImpl#getPidWithJavaApi() failed");
            return;
        }

        assertEquals(
                pid,
                new JnaPidProvider().getPid(),
                "JNA pid differs from java pid");
    }
}