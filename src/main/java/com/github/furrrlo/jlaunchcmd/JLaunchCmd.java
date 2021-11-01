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
                                new MacPsLaunchCmdService.Provider(), // Mac OS ps
                                new NixProcSelfLaunchCmdService.Provider(), // Unix /proc
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

    String getExecutable();

    String[] getArguments();
}
