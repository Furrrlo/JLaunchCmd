module jlaunchcmd {
    exports com.github.furrrlo.jlaunchcmd;
    uses com.github.furrrlo.jlaunchcmd.JLaunchCmdService.Provider;

    requires java.logging;
    requires java.management;
    requires static com.sun.jna.platform;
}