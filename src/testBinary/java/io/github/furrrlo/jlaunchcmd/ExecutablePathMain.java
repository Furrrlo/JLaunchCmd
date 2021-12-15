package io.github.furrrlo.jlaunchcmd;

public class ExecutablePathMain {
    public static void main(String[] args) throws Exception {
        if(args.length == 0) {
            System.out.println(JLaunchCmd.create().getExecutablePath());
            return;
        }

        final JLaunchCmdService.Provider provider = (JLaunchCmdService.Provider) Class.forName(args[0])
                .getDeclaredConstructor()
                .newInstance();
        if(!provider.isSupported())
            throw new Exception("Unsupported provider " + provider);

        System.out.println(provider.create().tryGetExecutablePath());
    }
}
