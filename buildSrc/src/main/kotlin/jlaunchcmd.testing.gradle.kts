@file:Suppress("UnstableApiUsage")

plugins {
    java
}

val testBinarySourceSet = sourceSets.register("testBinary")
configurations.named("testBinaryCompileClasspath") { extendsFrom(configurations.compileClasspath.get()) }
configurations.named("testBinaryRuntimeClasspath") { extendsFrom(configurations.runtimeClasspath.get()) }

val testBinaryJar = tasks.register<Jar>("testBinaryJar") {
    destinationDirectory.set(project.layout.buildDirectory.dir("libs"))
    archiveFileName.set("TestBinary-all.jar")

    from(testBinarySourceSet
        .flatMap { project.tasks.named<JavaCompile>(it.compileJavaTaskName) }
        .map { it.destinationDirectory })
    // Include libraries (basically jna if included)
    from(configurations
        .named("testBinaryRuntimeClasspath")
        .map { conf -> conf.resolve().map {
            if(it.isDirectory || !it.exists())
                it
            else
                project.zipTree(it) }
        }) {
        exclude("META-INF/**/*")
    }
}

dependencies {
    val junit = project.provider { project.ext["junitVersion"] as String }
    testImplementation(junit.map {"org.junit.jupiter:junit-jupiter-api:$it" })
    testImplementation(junit.map {"org.junit.jupiter:junit-jupiter-params:$it" })
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    val useJnaInTests = !project.hasProperty("noJnaInTests")
    val jna = project.provider { project.ext["jnaVersion"] as String }

    val jnaTestConfiguration = if(!useJnaInTests) "testCompileOnly" else "testImplementation"
    jnaTestConfiguration(jna.map { "net.java.dev.jna:jna-jpms:$it" })
    jnaTestConfiguration(jna.map { "net.java.dev.jna:jna-platform-jpms:$it" })

    // Depend on the main JLaunchCmd code
    "testBinaryImplementation"(sourceSets.main.map { it.output })
    if(useJnaInTests) {
        "testBinaryImplementation"(jna.map { "net.java.dev.jna:jna-jpms:$it" })
        "testBinaryImplementation"(jna.map { "net.java.dev.jna:jna-platform-jpms:$it" })
    }

    // Make the test SourceSet depend on the test binary so classes can be directly referenced
    // when specifying main classes, etc as args
    testImplementation(testBinarySourceSet.map { it.output })
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    systemProperty("junit.jna", !project.hasProperty("noJnaInTests"))

    // Task doesn't support configuration avoidance :I
    val testBinaryFile = testBinaryJar.map { it.archiveFile.get() }
    dependsOn(testBinaryJar)
    inputs.file(testBinaryFile)
    doFirst {
        systemProperty("junit.test.binary", testBinaryFile.map { it.asFile.absolutePath }.get())
    }
}
