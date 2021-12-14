package io.github.furrrlo.jlaunchcmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class JLaunchCmdImpl implements JLaunchCmd {

    private static final Logger LOGGER = Logger.getLogger(JLaunchCmdImpl.class.getName());

    private final Collection<JLaunchCmdService> services;

    JLaunchCmdImpl(Collection<JLaunchCmdService> services) {
        this.services = services;
    }

    @Override
    public String[] getLaunchCommand() {
        return tryForEachService(JLaunchCmdService::tryGetLaunchCommand);
    }

    @Override
    public String getShellLaunchCommand() {
        return tryForEachService(JLaunchCmdService::tryGetShellLaunchCommand);
    }

    @Override
    public String getExecutable() {
        return tryForEachService(JLaunchCmdService::tryGetExecutable);
    }

    @Override
    public String[] getArguments() {
        return tryForEachService(JLaunchCmdService::tryGetArguments);
    }

    private <T, E extends Exception> T tryForEachService(FunctionWithException<JLaunchCmdService, T, E> func) throws RuntimeException {
        final List<Throwable> exceptions = new ArrayList<>();

        for(JLaunchCmdService service : services) {
            try {
                return func.apply(service);
            } catch (Exception ex) {
                if(LOGGER.isLoggable(Level.FINE))
                    LOGGER.log(Level.FINE, "Service " + service + " failed", ex);

                exceptions.add(ex);
            }
        }

        final RuntimeException ex = new RuntimeException();
        exceptions.forEach(ex::addSuppressed);
        throw ex;
    }

    @FunctionalInterface
    private interface FunctionWithException<T, R, E extends Throwable> {
        R apply(T t) throws E;
    }
}
