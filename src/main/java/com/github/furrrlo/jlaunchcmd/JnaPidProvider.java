package com.github.furrrlo.jlaunchcmd;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.platform.win32.Kernel32;

class JnaPidProvider implements PidProvider {

    public JnaPidProvider() {
    }

    JnaPidProvider(@SuppressWarnings("unused") Void check) throws ClassNotFoundException, LinkageError {
        if(Platform.isWindows())
            Class.forName(Kernel32.INSTANCE.getClass().getName());
        else
            Class.forName(LibC.INSTANCE.getClass().getName());
    }

    @Override
    public long getPid() {
        return Platform.isWindows() ?
                Kernel32.INSTANCE.GetCurrentProcessId() :
                Platform.isMac() ?
                        SystemB.INSTANCE.getpid() :
                        LibC.INSTANCE.getpid();
    }

    private interface LibC extends com.sun.jna.platform.unix.LibC {

        LibC INSTANCE = Native.load(NAME, LibC.class);

        int getpid();
    }
}
