module jlaunchcmd {
    exports io.github.furrrlo.jlaunchcmd;
    uses io.github.furrrlo.jlaunchcmd.JLaunchCmdService.Provider;

    requires java.logging;
    requires java.management;
    requires static com.sun.jna.platform;
}