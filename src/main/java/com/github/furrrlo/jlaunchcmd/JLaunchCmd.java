package com.github.furrrlo.jlaunchcmd;

import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface JLaunchCmd {

    static JLaunchCmd create() {
        boolean useHandCrafterFallback = Boolean.parseBoolean(System.getProperty(
                "jlaunchcmd.useHandCraftedFallback",
                "true"));

        return new JLaunchCmdImpl(Stream.concat(
                        // User defined providers take precedence
                        StreamSupport.stream(
                                ServiceLoader.load(JLaunchCmdService.Provider.class).spliterator(),
                                false),
                        // Default providers
                        Stream.of(
                                new JavaProcessHandleLaunchCmdService.Provider(), // Java devs surely know more than me
                                new WinJnaLaunchCmdService.Provider(), // Windows Win32 API
                                new WinWmicLaunchCmdService.Provider(), // Windows wmic.exe
                                new MacJnaLaunchCmdService.Provider(), // Mac OS sysctl
                                new NixProcSelfLaunchCmdService.Provider(), // Unix /proc
                                new NixPsLaunchCmdService.Provider(), // Unix ps command
                                // Absolute last hope, if we get here we are desperate
                                useHandCrafterFallback ?
                                        new JavaHandCrafterLaunchCmdService.Provider() :
                                        null
                        ).filter(Objects::nonNull))
                .filter(JLaunchCmdService.Provider::isSupported)
                .map(JLaunchCmdService.Provider::create)
                .collect(Collectors.toList()));
    }

    String[] getLaunchCommand();

    /**
     * Return the command used to launch this process properly escaped so that it can be run in a shell.
     *
     * This command is only guaranteed to work properly on Windows (where it should be used the most because of the
     * parameters requested by functions such as ShellExecute, etc), while on other OSes it will return
     * a best effort attempt.
     *
     * @return command used to launch this process
     */
    String getShellLaunchCommand();

    String getExecutable();

    String[] getArguments();
}
