plugins {
    `java-library`
    id("jlaunchcmd.java-conventions")
    id("jlaunchcmd.testing")
    id("jlaunchcmd.mrjar")
    id("jlaunchcmd.publishing")
}

group = "io.github.furrrlo"
version = "1.1" + releaseParams.snapshotSuffix

ext["jnaVersion"] = "5.9.0"
ext["junitVersion"] = "5.8.1"

dependencies {
    val jna = project.ext["jnaVersion"] as String
    compileOnly("net.java.dev.jna:jna-jpms:$jna")
    compileOnly("net.java.dev.jna:jna-platform-jpms:$jna")
}
