package io.github.furrrlo.jlaunchcmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RunTestBinary {

    private static final Path TEST_BINARY = Paths.get(System.getProperty("junit.test.binary"));
    private static final Path JAVA_EXECUTABLE_PATH = Paths.get(System.getProperty("java.home"))
            .resolve("bin")
            .resolve(System.getProperty("os.name").toLowerCase().contains("win") ? "java.exe" : "java");

    public static String runWithAbsoluteExecutablePath(String mainClass, String... args) throws IOException, InterruptedException, TimeoutException {
        return doRun(new ProcessBuilder()
                .command(Stream.concat(
                        Stream.of(
                                JAVA_EXECUTABLE_PATH.toAbsolutePath().toString(),
                                "-Djunit.test.binary=" + TEST_BINARY.toAbsolutePath(),
                                "-cp", TEST_BINARY.toAbsolutePath().toString(),
                                mainClass
                        ),
                        Arrays.stream(args)
                ).collect(Collectors.toList()))
                .redirectErrorStream(true));
    }

    public static String runWithRelativeExecutablePath(String mainClass, String... args) throws IOException, InterruptedException, TimeoutException {
        return doRun(new ProcessBuilder()
                .command(Stream.concat(
                        Stream.of(
                                JAVA_EXECUTABLE_PATH.toAbsolutePath().toString(),
                                "-Djunit.test.binary=" + TEST_BINARY.toAbsolutePath(),
                                "-cp", TEST_BINARY.toAbsolutePath().toString(),
                                "RelativeExecutablePathMain",
                                mainClass
                        ),
                        Arrays.stream(args)
                ).collect(Collectors.toList()))
                .directory(JAVA_EXECUTABLE_PATH.getParent().toFile())
                .redirectErrorStream(true));
    }

    public static String doRun(ProcessBuilder processBuilder) throws IOException, InterruptedException, TimeoutException {
        System.out.println("Running command: " + processBuilder.command());

        final Process process = processBuilder.start();
        final String output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"))
                .trim();

        if(!process.waitFor(10, TimeUnit.SECONDS)) {
            System.err.println(output);
            throw new TimeoutException("Process waitFor time has elapsed");
        }

        if(process.exitValue() != 0) {
            System.err.println(output);
            throw new IOException("Process exited with value " + process.exitValue());
        }

        return output;
    }
}
