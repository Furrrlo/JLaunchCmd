package io.github.furrrlo.jlaunchcmd;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.win32.W32APITypeMapper;

class WinJnaLaunchCmdService implements JLaunchCmdService {

    static class Provider implements JLaunchCmdService.Provider {

        @Override
        public boolean isSupported() {
            try {
                Class.forName(WinJnaLaunchCmdService.Kernel32.INSTANCE.getClass().getName());
            } catch (ClassNotFoundException | LinkageError ex) {
                return false;
            }

            return Platform.isWindows();
        }

        @Override
        public JLaunchCmdService create() {
            return new WinJnaLaunchCmdService();
        }
    }

    private final CommandLineSplitter commandLineSplitter;

    public WinJnaLaunchCmdService() {
        this(CommandLineSplitter.INSTANCE);
    }

    public WinJnaLaunchCmdService(CommandLineSplitter commandLineSplitter) {
        this.commandLineSplitter = commandLineSplitter;
    }

    @Override
    public String[] tryGetLaunchCommand() throws Exception {
        return commandLineSplitter.splitCommand(tryGetShellLaunchCommand());
    }

    @Override
    public String tryGetShellLaunchCommand() throws Exception {
        final Pointer commandLinePtr = Kernel32.INSTANCE.GetCommandLine();
        final String commandLine = W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE ?
                commandLinePtr.getWideString(0) :
                commandLinePtr.getString(0);
        return commandLine;
    }

    private interface Kernel32 extends com.sun.jna.platform.win32.Kernel32 {

        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class, W32APIOptions.DEFAULT_OPTIONS);

        /**
         * Retrieves the command-line string for the current process.
         *
         * The lifetime of the returned value is managed by the system, applications should not free or modify this value.
         * To convert the command line to an argv style array of strings, pass the result from GetCommandLineW to CommandLineToArgvW.
         *
         * Note  The name of the executable in the command line that the operating system provides to a process is not
         * necessarily identical to that in the command line that the calling process gives to the CreateProcess function.
         * The operating system may prepend a fully qualified path to an executable name that is provided without a
         * fully qualified path.
         *
         * See <a>https://docs.microsoft.com/en-us/windows/win32/api/processenv/nf-processenv-getcommandlinew</a>
         *
         * @return The return value is a pointer to the command-line string for the current process.
         */
        Pointer GetCommandLine();
    }
}