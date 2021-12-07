package io.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class NixCommandLineSplitterTest {

    @ParameterizedTest
    @MethodSource("provideCommands")
    @EnabledOnOs({ OS.MAC, OS.LINUX })
    void splitTheSameWay(String cmd) {
        assumeTrue(Boolean.getBoolean("junit.jna"), "Missing JNA");

        String[] expected, actual;
        assertArrayEquals(
                expected = new NixJnaCommandLineSplitter().splitCommand(cmd),
                actual = new NixCommandLineSplitter().splitCommand(cmd),
                String.format("Split wrong (command: '%s',\nexpected:\t'%s',\nactual:\t\t'%s'\n)", cmd, Arrays.toString(expected), Arrays.toString(actual)));
    }

    private static Stream<Arguments> provideCommands() {
        // Don't use stuff that can get expanded like variables, paths, etc as wordexp will actually expand those
        return Stream.of(
                Arguments.of("" +
                        "/Users/runner/hostedtoolcache/Java_Adopt_jdk/11.0.12-7/x64/Contents/Home/bin/java " +
                        "-Djunit.jna=true " +
                        "-Dorg.gradle.internal.worker.tmpdir=/Users/runner/work/JLaunchCmd/JLaunchCmd/build/tmp/test/work " +
                        "-Dorg.gradle.native=false " +
                        "@/Users/runner/.gradle/.tmp/gradle-worker-classpath13312965562678943386txt " +
                        "-Xmx512m " +
                        "-Dfile.encoding=UTF-8 " +
                        "-Duser.country=US " +
                        "-Duser.language=en " +
                        "-Duser.variant " +
                        "-ea worker.org.gradle.process.internal.worker.GradleWorkerMain " +
                        "'Gradle Test Executor 1'")
        );
    }
}