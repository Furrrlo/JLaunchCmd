package com.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PidProviderImplTest {

    @Test
    void reflectionsWorking() {
        assertDoesNotThrow(() -> new PidProviderImpl().getPidWithJavaApi(), "Reflections failed");
    }
}