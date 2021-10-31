package com.github.furrrlo.jlaunchcmd;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class JavaHandCrafterLaunchCmdService implements JLaunchCmdService {

    static class Provider implements JLaunchCmdService.Provider {

        @Override
        public boolean isSupported() {
            return true;
        }

        @Override
        public JLaunchCmdService create() {
            return new JavaHandCrafterLaunchCmdService();
        }
    }

    @Override
    public String[] tryGetLaunchCommand() throws Exception {
        try {
            final List<String> command = new ArrayList<>();

            appendJavaExe(command);
            appendJVMArgs(command);
            appendEntryPoint(command);
            appendArgs(command);

            return command.toArray(new String[0]);
        } catch(Exception ex) {
            throw new Exception("Couldn't hand craft launch command", ex);
        }
    }

    @Override
    public String tryGetExecutable() throws Exception {
        return getJavaExe();
    }

    @Override
    public String[] tryGetArguments() throws Exception {
        try {
            final List<String> command = new ArrayList<>();

            appendJVMArgs(command);
            appendEntryPoint(command);
            appendArgs(command);

            return command.toArray(new String[0]);
        } catch(Exception ex) {
            throw new Exception("Couldn't hand craft launch command", ex);
        }
    }

    private String getJavaExe() {
        // Uses java.home, not the one actually used to launch ;-;
        return System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
    }

    private void appendJavaExe(Collection<String> cmd) {
        cmd.add(getJavaExe());
    }

    private void appendJVMArgs(Collection<String> cmd) {
        final List<String> jvmArgs = new ArrayList<>(ManagementFactory.getRuntimeMXBean().getInputArguments());

        final String javaToolOptions = System.getenv("JAVA_TOOL_OPTIONS");
        if(javaToolOptions != null)
            jvmArgs.removeAll(Arrays.asList(javaToolOptions.split(" ")));

        cmd.addAll(jvmArgs);
    }

    private void appendEntryPoint(Collection<String> cmd) {

        final Optional<String[]> sjc = getSplitSunJavaCommand();
        if(sjc.isPresent()) {
            final String entryPoint = sjc.get()[0];
            // Weak test, "java -jar ./not_a_jar" would fail
            if(entryPoint.endsWith(".jar") || new File(entryPoint).exists())
                cmd.add("-jar");
            else
                appendClassPath(cmd);
            cmd.add(entryPoint);
            return;
        }

        final String mainClassName = getMainClassFromStackTrace()
                .orElseThrow(() -> new RuntimeException("Failed to find the entry point"));
        // No idea what happens here if it was launched from a jar
        appendClassPath(cmd);
        cmd.add(mainClassName);
    }

    private Optional<String> getMainClassFromStackTrace() {
        return Thread.getAllStackTraces().entrySet().stream()
                // Neither of those are portable ways but whatever
                // https://stackoverflow.com/a/45261595
                // https://stackoverflow.com/a/9063262
                .filter(e -> e.getKey().getName().equals("main") || e.getKey().getId() == 1)
                .map(Map.Entry::getValue)
                .map(stackTrace -> stackTrace[stackTrace.length - 1])
                .filter(ste -> ste.getMethodName().equals("main"))
                .findFirst()
                .map(StackTraceElement::getClassName);
    }

    private void appendClassPath(Collection<String> cmd) {
        cmd.add("-cp");
        cmd.add(ManagementFactory.getRuntimeMXBean().getClassPath());
    }

    private void appendArgs(Collection<String> cmd) {
        final String[] command = getSplitSunJavaCommand()
                .orElseThrow(() -> new RuntimeException("Failed to find args"));
        Collections.addAll(cmd, Arrays.copyOfRange(command, 1, command.length));
    }

    private Optional<String[]> getSplitSunJavaCommand() {
        final String sjc = System.getProperty("sun.java.command");
        if(sjc == null)
            return Optional.empty();
        return Optional.of(splitSunJavaCommand(sjc));
    }

    /**
     * Attempts to correctly split the given "sun.java.command" property.
     *
     * This assumes that all the args follow the convention "--<key_without_spaces> <value>" cause whitespaces are ambiguous
     * See https://github.com/AdoptOpenJDK/openjdk-jdk8u/blob/0a48292eb17b3a7adc755bfaf7b928a1b73e40f7/jdk/src/share/bin/java.c#L1530
     *
     * @param sjc "sun.java.command" property as returned by {@link System#getProperty(String)}
     * @return the split args
     */
    static String[] splitSunJavaCommand(String sjc) {
        // Retain the delimiter (from https://stackoverflow.com/a/2206432)
        final String withDelimiter = "(?=%s)";
        final String[] split = sjc.split(String.format(withDelimiter, " (--|-)"));
        return Stream.concat(
                        Stream.of(split[0]), // Keep the entry point as is
                        Arrays.stream(split)
                                .skip(1) // Skip the entry point
                                .map(s -> s.substring(1)) // Remove the first space, which is part of the delimiter
                                .flatMap(s -> {
                                    final Matcher matcher = Pattern.compile("\\s").matcher(s);
                                    if(!matcher.find())
                                        return Arrays.stream(new String[] { s });

                                    final int firstSpaceIdx = matcher.start();
                                    return Arrays.stream(new String[] {
                                            s.substring(0, firstSpaceIdx),
                                            s.substring(firstSpaceIdx + 1)
                                    });
                                }))
                .toArray(String[]::new);
    }
}
