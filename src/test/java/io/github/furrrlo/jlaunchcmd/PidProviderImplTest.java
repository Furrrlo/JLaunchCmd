package io.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import static org.junit.jupiter.api.Assertions.*;

class PidProviderImplTest {

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void reflectionsWorking() {
        assertDoesNotThrow(() -> new PidProviderImpl().getPidWithJavaApi(), "Reflections failed");
    }
}