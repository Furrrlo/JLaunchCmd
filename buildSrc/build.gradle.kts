plugins {
    `kotlin-dsl`
}

group = "io.github.furrrlo"

dependencies {
    val voteStageReleaseVer = "1.77"
    implementation("com.github.vlsi.gradle:crlf-plugin:$voteStageReleaseVer")
    implementation("com.github.vlsi.gradle:gradle-extensions-plugin:$voteStageReleaseVer")
    implementation("com.github.vlsi.gradle:stage-vote-release-plugin:$voteStageReleaseVer")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}
