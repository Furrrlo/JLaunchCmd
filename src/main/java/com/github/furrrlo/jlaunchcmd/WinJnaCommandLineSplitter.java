package com.github.furrrlo.jlaunchcmd;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Shell32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.ptr.IntByReference;

class WinJnaCommandLineSplitter implements CommandLineSplitter {

    public WinJnaCommandLineSplitter() {
    }

    WinJnaCommandLineSplitter(@SuppressWarnings("unused") Void check) throws ClassNotFoundException, LinkageError {
        Class.forName(Kernel32.INSTANCE.getClass().getName());
        Class.forName(Shell32.INSTANCE.getClass().getName());
    }

    @Override
    public String[] splitCommand(String cmd) {
        final IntByReference argc = new IntByReference();
        final Pointer argv = Shell32.INSTANCE.CommandLineToArgvW(new WString(cmd), argc);
        if(argv == null)
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());

        try {
            return argv.getWideStringArray(0, argc.getValue());
        } finally {
            Kernel32.INSTANCE.LocalFree(argv);
        }
    }
}
