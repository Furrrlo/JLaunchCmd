package com.github.furrrlo.jlaunchcmd;

class CommandLineSplitterImpl implements CommandLineSplitter {

    private final CommandLineSplitter delegate;

    public CommandLineSplitterImpl() {
        this.delegate = createDelegate();
    }

    private static CommandLineSplitter createDelegate() {
        final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

        try {
            if(isWindows)
                return new WinJnaCommandLineSplitter(null);
        } catch (ClassNotFoundException | LinkageError ignored) {
            // Go through
        }

        return isWindows ?
                new WinCommandLineSplitter() :
                new NixCommandLineSplitter();
    }

    @Override
    public String[] splitCommand(String cmd) {
        return delegate.splitCommand(cmd);
    }
}
