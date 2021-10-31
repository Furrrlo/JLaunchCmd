package com.github.furrrlo.jlaunchcmd;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.linux.ErrNo;
import com.sun.jna.platform.unix.LibCAPI;
import com.sun.jna.ptr.IntByReference;

class MacJnaLaunchCmdService implements JLaunchCmdService {

    static class Provider implements JLaunchCmdService.Provider {

        @Override
        public boolean isSupported() {
            try {
                Class.forName(SystemB.INSTANCE.getClass().getName());
            } catch (ClassNotFoundException | LinkageError ex) {
                return false;
            }

            return Platform.isMac();
        }

        @Override
        public JLaunchCmdService create() {
            return new MacJnaLaunchCmdService();
        }
    }

    private static final int CTL_KERN = 1;
    private static final int KERN_ARGMAX = 8;
    private static final int KERN_PROCARGS2 = 49;

    private final PidProvider pidProvider;

    public MacJnaLaunchCmdService() {
        this(PidProvider.INSTANCE);
    }

    public MacJnaLaunchCmdService(PidProvider pidProvider) {
        this.pidProvider = pidProvider;
    }

    @Override
    public String[] tryGetLaunchCommand() throws Exception {

        final IntByReference maxArgs = new IntByReference();
        if (SystemB.INSTANCE.sysctl(
                new int[] { CTL_KERN, KERN_ARGMAX }, 2,
                maxArgs.getPointer(), new LibCAPI.size_t.ByReference(SystemB.INT_SIZE),
                null, new LibCAPI.size_t(0)) < 0)
            throw perror("sysctl(KERN_ARGMAX) failed");

        final long pid = pidProvider.getPid();
        final Memory args = new Memory(maxArgs.getValue());
        if (SystemB.INSTANCE.sysctl(
                new int[] { CTL_KERN, KERN_PROCARGS2, (int) pid }, 3,
                args, new LibCAPI.size_t.ByReference(maxArgs.getValue()),
                null, new LibCAPI.size_t(0)) < 0)
            throw perror("sysctl(KERN_PROCARGS2) failed");

        final int nArgs = args.getInt(0);
        final String[] cmd = new String[nArgs];

        Pointer currArg = args.getPointer(SystemB.INT_SIZE);
        for(int i = 0; i < nArgs; i++) {
            if(Pointer.nativeValue(currArg) >= Pointer.nativeValue(args) + maxArgs.getValue())
                throw new IndexOutOfBoundsException(String.format(
                        "Returned process args went over maxArgs (currArg: %d, maxArg: %d, nArgs: %d, index: %d)",
                        Pointer.nativeValue(currArg),
                        Pointer.nativeValue(args) + maxArgs.getValue(),
                        nArgs, i));

            cmd[i] = currArg.getString(0);
            currArg = currArg.getPointer(SystemB.INSTANCE.strlen(currArg));
        }

        return cmd;
    }

    private Exception perror(String s) throws Exception {
        int errno = Native.getLastError();
        throw new Exception(s + ": " + SystemB.INSTANCE.strerror(errno) + " (" + errno + ")");
    }

    private interface SystemB extends com.sun.jna.platform.mac.SystemB {

        SystemB INSTANCE = Native.load("System", SystemB.class);

        String strerror(int errnum);

        int strlen(Pointer pointer);
    }
}
