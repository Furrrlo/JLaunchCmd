package com.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JnaPidProviderTest {

    @Test
    void sameAsJavaProcessHandlePid() {
        long pid;
        try {
            pid = new PidProviderImpl().getPidWithJavaApi();
        } catch (Throwable t) {
            Assumptions.assumeTrue(false, "PidProviderImpl#getPidWithJavaApi() failed");
            return;
        }

        assertEquals(
                pid,
                new JnaPidProvider().getPid(),
                "JNA pid differs from java pid");
    }
}