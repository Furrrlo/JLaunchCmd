@file:Suppress("UnstableApiUsage")

plugins {
    java
}

dependencies {
    val jna = project.provider { project.ext["jnaVersion"] as String }
    val jnaTestConfiguration = if(project.hasProperty("noJnaInTests")) "testCompileOnly" else "testImplementation"
    jnaTestConfiguration(jna.map { "net.java.dev.jna:jna-jpms:$it" })
    jnaTestConfiguration(jna.map { "net.java.dev.jna:jna-platform-jpms:$it" })

    val junit = project.provider { project.ext["junitVersion"] as String }
    testImplementation(junit.map {"org.junit.jupiter:junit-jupiter-api:$it" })
    testImplementation(junit.map {"org.junit.jupiter:junit-jupiter-params:$it" })
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    systemProperty("junit.jna", !project.hasProperty("noJnaInTests"))
}
