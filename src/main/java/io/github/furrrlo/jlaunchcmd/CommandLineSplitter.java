package io.github.furrrlo.jlaunchcmd;

interface CommandLineSplitter {

    CommandLineSplitter INSTANCE = new CommandLineSplitterImpl();

    String[] splitCommand(String cmd);
}
