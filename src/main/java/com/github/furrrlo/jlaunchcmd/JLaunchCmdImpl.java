package com.github.furrrlo.jlaunchcmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

class JLaunchCmdImpl implements JLaunchCmd {

    private static final Logger LOGGER = Logger.getLogger(JLaunchCmdImpl.class.getName());

    private final Collection<JLaunchCmdService> services;

    JLaunchCmdImpl(Collection<JLaunchCmdService> services) {
        this.services = services;
    }

    @Override
    public String[] getLaunchCommand() {
        final List<Throwable> exceptions = new ArrayList<>();

        for(JLaunchCmdService service : services) {
            try {
                return service.tryGetLaunchCommand();
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

    @Override
    public String getShellLaunchCommand() {
        final List<Throwable> exceptions = new ArrayList<>();

        for(JLaunchCmdService service : services) {
            try {
                return service.tryGetShellLaunchCommand();
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

    @Override
    public String getExecutable() {
        final List<Throwable> exceptions = new ArrayList<>();

        for(JLaunchCmdService service : services) {
            try {
                return service.tryGetExecutable();
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

    @Override
    public String[] getArguments() {
        final List<Throwable> exceptions = new ArrayList<>();

        for(JLaunchCmdService service : services) {
            try {
                return service.tryGetArguments();
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
}
