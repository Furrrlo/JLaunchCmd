package io.github.furrrlo.jlaunchcmd;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Optional;

class JavaProcessHandleLaunchCmdService implements JLaunchCmdService {

    static class Provider implements JLaunchCmdService.Provider {

        private JavaProcessHandleLaunchCmdService service;

        public Provider() {
            try {
                this.service = new JavaProcessHandleLaunchCmdService();
            } catch (ReflectiveOperationException ignored) {
            }
        }

        @Override
        public boolean isSupported() {
            return service != null;
        }

        @Override
        public JLaunchCmdService create() {
            return service;
        }
    }

    private final CommandLineSplitter commandLineSplitter;

    private final MethodHandle processHandleCurrentMethod;
    private final MethodHandle processHandleInfoMethod;
    private final MethodHandle processHandleInfoCommandMethod;
    private final MethodHandle processHandleInfoArgumentsMethod;
    private final MethodHandle processHandleInfoCommandLineMethod;

    public JavaProcessHandleLaunchCmdService() throws ReflectiveOperationException {
        this(CommandLineSplitter.INSTANCE);
    }

    public JavaProcessHandleLaunchCmdService(CommandLineSplitter commandLineSplitter) throws ReflectiveOperationException {
        this.commandLineSplitter = commandLineSplitter;

        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        final Class<?> processHandleClazz = Class.forName("java.lang.ProcessHandle");
        processHandleCurrentMethod = lookup.unreflect(processHandleClazz.getMethod("current"));
        processHandleInfoMethod = lookup.unreflect(processHandleClazz.getMethod("info"));
        final Class<?> infoClazz = Class.forName("java.lang.ProcessHandle$Info");
        processHandleInfoCommandMethod = lookup.unreflect(infoClazz.getMethod("command"));
        processHandleInfoArgumentsMethod = lookup.unreflect(infoClazz.getMethod("arguments"));
        processHandleInfoCommandLineMethod = lookup.unreflect(infoClazz.getMethod("commandLine"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public String[] tryGetLaunchCommand() throws Exception {

        final Optional<String[]> arguments;
        final Optional<String> command, commandLine;
        try {
            final Object currProcInfo = processHandleInfoMethod.invoke(processHandleCurrentMethod.invoke());
            command = (Optional<String>) processHandleInfoCommandMethod.invoke(currProcInfo);
            arguments = (Optional<String[]>) processHandleInfoArgumentsMethod.invoke(currProcInfo);
            commandLine = (Optional<String>) processHandleInfoCommandLineMethod.invoke(currProcInfo);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to use Java Process API", t);
        }

        if (command.isPresent() && arguments.isPresent()) {
            final String[] args = arguments.get();
            final String[] launchCmd = new String[args.length + 1];
            launchCmd[0] = command.get();
            System.arraycopy(args, 0, launchCmd, 1, args.length);
            return launchCmd;
        }

        return commandLineSplitter.splitCommand(commandLine
                .orElseThrow(() -> new Exception("Java was not able to determine the process commandLine")));
    }

    @Override
    @SuppressWarnings("unchecked")
    public String tryGetExecutable() throws Exception {
        final Optional<String> command;
        try {
            final Object currProcInfo = processHandleInfoMethod.invoke(processHandleCurrentMethod.invoke());
            command = (Optional<String>) processHandleInfoCommandMethod.invoke(currProcInfo);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to use Java Process API", t);
        }

        if(command.isPresent())
            return command.get();
        return JLaunchCmdService.super.tryGetExecutable();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String[] tryGetArguments() throws Exception {
        final Optional<String[]> arguments;
        try {
            final Object currProcInfo = processHandleInfoMethod.invoke(processHandleCurrentMethod.invoke());
            arguments = (Optional<String[]>) processHandleInfoArgumentsMethod.invoke(currProcInfo);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to use Java Process API", t);
        }

        if(arguments.isPresent())
            return arguments.get();
        return JLaunchCmdService.super.tryGetArguments();
    }
}
