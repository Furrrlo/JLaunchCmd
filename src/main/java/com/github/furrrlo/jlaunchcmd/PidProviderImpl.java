package com.github.furrrlo.jlaunchcmd;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongSupplier;

class PidProviderImpl implements PidProvider {

    private MethodHandle processHandleCurrentMethod;
    private MethodHandle processHandlePidMethod;
    private Throwable methodHandleException;

    private PidProvider jnaPidProvider;
    private Throwable jnaException;

    public PidProviderImpl() {
        try {
            final Class<?> clazz = Class.forName("java.lang.ProcessHandle");
            processHandleCurrentMethod = MethodHandles.lookup().unreflect(clazz.getMethod("current"));
            processHandlePidMethod = MethodHandles.lookup().unreflect(clazz.getMethod("pid"));
        } catch (ReflectiveOperationException ex) {
            methodHandleException = ex;
        }

        try {
            jnaPidProvider = new JnaPidProvider(null);
        } catch (ClassNotFoundException | LinkageError ex) {
            jnaException = ex;
        }
    }

    @Override
    public long getPid() {
        final List<Throwable> exceptions = new ArrayList<>();

        for(LongSupplier m : Arrays.<LongSupplier>asList(
                this::getPidWithJavaApi,
                this::getPidWithJna,
                this::getPidWithReflections,
                this::parsePidFromVmName
        )) {
            try {
                return m.getAsLong();
            } catch (Exception ex) {
                exceptions.add(ex);
            }
        }

        final RuntimeException ex = new RuntimeException("Failed to get current process PID");
        exceptions.forEach(ex::addSuppressed);
        throw ex;
    }

    long getPidWithJavaApi() {
        if(processHandleCurrentMethod == null || processHandlePidMethod == null) {
            if(methodHandleException != null)
                throw new RuntimeException("Java version is < 9", methodHandleException);
            throw new RuntimeException("Java version is < 9");
        }

        try {
            return (long) processHandlePidMethod.invoke(processHandleCurrentMethod.invoke());
        } catch (Throwable t) {
            throw new RuntimeException("Failed to use Java Process API", t);
        }
    }

    private long getPidWithJna() {
        if(jnaPidProvider == null) {
            if(jnaException != null)
                throw new RuntimeException("Couldn't find JNA on the classpath", jnaException);
            throw new RuntimeException("Couldn't find JNA on the classpath");
        }

        try {
            return jnaPidProvider.getPid();
        } catch (Throwable ex) {
            throw new RuntimeException("Failed to get PID using JNA", ex);
        }
    }

    private long getPidWithReflections() {
        // From https://stackoverflow.com/a/12066696
        // This is specific to only some VMs
        try {
            final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
            return AccessController.doPrivileged((PrivilegedExceptionAction<Long>) () -> {
                final Field jvm = runtime.getClass().getDeclaredField("jvm");

                jvm.setAccessible(true);
                final Object mgmt = jvm.get(runtime);

                final Method getProcessId = mgmt.getClass().getDeclaredMethod("getProcessId");
                getProcessId.setAccessible(true);

                final Integer processId = (Integer) getProcessId.invoke(mgmt);
                return processId == null ? null : processId.longValue();
            });
        } catch (PrivilegedActionException ex) {
            throw new RuntimeException("Couldn't use reflections on ManagementFactory", ex);
        }
    }

    private long parsePidFromVmName() {
        // From https://stackoverflow.com/a/7690178
        // something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmName.indexOf('@');
        if(index < 1)
            throw new IllegalStateException("VM name does not include PID");

        try {
            return Long.parseLong(jvmName.substring(0, index));
        } catch(NumberFormatException e) {
            throw new IllegalStateException("VM name does not include PID", e);
        }
    }
}
