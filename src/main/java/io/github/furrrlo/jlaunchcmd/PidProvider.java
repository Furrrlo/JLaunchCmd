package io.github.furrrlo.jlaunchcmd;

interface PidProvider {

    PidProvider INSTANCE = new PidProviderImpl();

    long getPid();
}
