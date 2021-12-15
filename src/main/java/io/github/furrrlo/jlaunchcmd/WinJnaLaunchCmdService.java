package io.github.furrrlo.jlaunchcmd;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.win32.W32APITypeMapper;

import java.nio.file.Path;
import java.nio.file.Paths;

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

    @Override
    public Path tryGetExecutablePath() {
        Memory buff = new Memory(WinDef.MAX_PATH);
        for(int tries = 0; ; tries++) {
            int copiedSize = Kernel32.INSTANCE.GetModuleFileName(null, buff, (int) buff.size());
            if(copiedSize == buff.size()) {
                int lastErr = Kernel32.INSTANCE.GetLastError();
                if(tries < 2 && (lastErr == WinError.ERROR_INSUFFICIENT_BUFFER || lastErr == WinError.ERROR_SUCCESS)) {
                    buff = new Memory(buff.size() * 2);
                    continue;
                }

                throw new Win32Exception(lastErr);
            }

            if(copiedSize == 0)
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());

            return Paths.get(W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE ?
                    buff.getWideString(0) :
                    buff.getString(0));
        }
    }

    private interface Kernel32 extends com.sun.jna.platform.win32.Kernel32 {

        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class, W32APIOptions.DEFAULT_OPTIONS);

        /**
         * Retrieves the command-line string for the current process.
         * <p>
         * The lifetime of the returned value is managed by the system, applications should not free or modify this value.
         * To convert the command line to an argv style array of strings, pass the result from GetCommandLineW to CommandLineToArgvW.
         * <p>
         * Note  The name of the executable in the command line that the operating system provides to a process is not
         * necessarily identical to that in the command line that the calling process gives to the CreateProcess function.
         * The operating system may prepend a fully qualified path to an executable name that is provided without a
         * fully qualified path.
         * <p>
         * See <a>https://docs.microsoft.com/en-us/windows/win32/api/processenv/nf-processenv-getcommandlinew</a>
         *
         * @return The return value is a pointer to the command-line string for the current process.
         */
        Pointer GetCommandLine();

        /**
         * Retrieves the fully qualified path for the file that contains the specified module.
         * The module must have been loaded by the current process.
         * <p>
         * To locate the file for a module that was loaded by another process, use the GetModuleFileNameEx function.
         *
         * @param hModule    A handle to the loaded module whose path is being requested.
         *                   If this parameter is NULL, GetModuleFileName retrieves the path of the executable file
         *                   of the current process.
         *                   <p>
         *                   The GetModuleFileName function does not retrieve the path for modules that were loaded
         *                   using the LOAD_LIBRARY_AS_DATAFILE flag. For more information, see LoadLibraryEx.
         * @param lpFilename A pointer to a buffer that receives the fully qualified path of the module.
         *                   If the length of the path is less than the size that the nSize parameter specifies,
         *                   the function succeeds and the path is returned as a null-terminated string.
         *                   <p>
         *                   If the length of the path exceeds the size that the nSize parameter specifies,
         *                   the function succeeds and the string is truncated to nSize characters including
         *                   the terminating null character.
         *                   <p>
         *                   Windows XP:  The string is truncated to nSize characters and is not null-terminated.
         *                   <p>
         *                   The string returned will use the same format that was specified when the module was loaded.
         *                   Therefore, the path can be a long or short file name, and can use the prefix \\?\.
         *                   For more information, see Naming a File.
         * @param nSize      The size of the lpFilename buffer, in TCHARs.
         * @return If the function succeeds, the return value is the length of the string that is copied to the buffer,
         * in characters, not including the terminating null character. If the buffer is too small to hold the module
         * name, the string is truncated to nSize characters including the terminating null character, the function
         * returns nSize, and the function sets the last error to ERROR_INSUFFICIENT_BUFFER.
         * <p>
         * Windows XP:  If the buffer is too small to hold the module name, the function returns nSize.
         * The last error code remains ERROR_SUCCESS.
         * If nSize is zero, the return value is zero and the last error code is ERROR_SUCCESS.
         * <p>
         * If the function fails, the return value is 0 (zero). To get extended error information, call GetLastError.
         */
        int GetModuleFileName(HMODULE hModule, Pointer lpFilename, int nSize);
    }
}
