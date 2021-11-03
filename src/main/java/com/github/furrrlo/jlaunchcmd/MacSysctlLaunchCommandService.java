package com.github.furrrlo.jlaunchcmd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class MacSysctlLaunchCommandService implements JLaunchCmdService {

    static class Provider implements JLaunchCmdService.Provider {

        @Override
        public boolean isSupported() {
            final String osName = System.getProperty("os.name").toLowerCase();
            return osName.contains("mac") || osName.contains("darwin");
        }

        @Override
        public JLaunchCmdService create() {
            return new MacSysctlLaunchCommandService();
        }
    }

    private static final int SIZE_OF_KINFO_PROC_32 = 492;
    private static final int SIZE_OF_KINFO_PROC_64 = 648;
    private static final int SIZE_OF_KINFO_PROC;
    private static final int KINFO_PROC_PID_OFFSET_32 = 24;
    private static final int KINFO_PROC_PID_OFFSET_64 = 40;
    private static final int KINFO_PROC_PID_OFFSET;

    static {
        String arch = System.getProperty("sun.arch.data.model");
        if ("64".equals(arch)) {
            SIZE_OF_KINFO_PROC = SIZE_OF_KINFO_PROC_64;
            KINFO_PROC_PID_OFFSET = KINFO_PROC_PID_OFFSET_64;
        } else {
            SIZE_OF_KINFO_PROC = SIZE_OF_KINFO_PROC_32;
            KINFO_PROC_PID_OFFSET = KINFO_PROC_PID_OFFSET_32;
        }
    }

    private final PidProvider pidProvider;

    public MacSysctlLaunchCommandService() {
        this(PidProvider.INSTANCE);
    }

    public MacSysctlLaunchCommandService(PidProvider pidProvider) {
        this.pidProvider = pidProvider;
    }

    @Override
    public String[] tryGetLaunchCommand() throws Exception {
        final long pid = pidProvider.getPid();

        final ByteBuffer procAll;
        try {
            final String cmd = "sysctl -b kern.proc.all";
            final Process process = Runtime.getRuntime().exec(cmd);

            final byte[] bytes = readAllBytes(process.getInputStream());
            procAll = ByteBuffer.wrap(bytes);
            System.out.println("bytes " + bytes.length);

            if(!process.waitFor(1, TimeUnit.SECONDS))
                throw new TimeoutException("Process waitFor time has elapsed");
        } catch(IOException ex) {
            throw new IOException("ps not available", ex);
        }

        System.out.println(pid);
        System.out.println("procAll " + procAll.limit());
        for(int offset = 0; offset < procAll.limit(); offset += SIZE_OF_KINFO_PROC) {
            int currPid = procAll.getInt(offset + KINFO_PROC_PID_OFFSET);
            System.out.println(currPid);
        }

        return new String[] {};
    }

    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buf = new byte[8192];
            int readLen;
            while ((readLen = inputStream.read(buf, 0, buf.length)) != -1)
                outputStream.write(buf, 0, readLen);

            return outputStream.toByteArray();
        }
    }
}
