import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RelativeExecutablePathMain {

    private static final Path TEST_BINARY = Paths.get(System.getProperty("junit.test.binary"));

    public static void main(String[] args) throws IOException, InterruptedException {
        System.exit(new ProcessBuilder()
                .command(Stream.concat(
                        Stream.of(
                                "java",
                                "-Djunit.test.binary=" + TEST_BINARY.toAbsolutePath(),
                                "-cp", TEST_BINARY.toAbsolutePath().toString(),
                                args[0]
                        ),
                        Arrays.stream(args).skip(1)
                ).collect(Collectors.toList()))
                .redirectErrorStream(true)
                .inheritIO()
                .start()
                .waitFor());
    }
}
