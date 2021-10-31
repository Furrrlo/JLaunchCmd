module jlaunchcmd {
    uses com.github.furrrlo.jlaunchcmd.JLaunchCmdService.Provider;

    requires java.logging;
    requires java.management;
    requires static com.sun.jna.platform;
}