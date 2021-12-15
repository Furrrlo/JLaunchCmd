import com.github.vlsi.gradle.crlf.CrLfSpec
import com.github.vlsi.gradle.crlf.LineEndings

plugins {
    `java-library`
    id("com.github.vlsi.crlf") version "1.77"
    id("com.github.vlsi.gradle-extensions") version "1.77"
    id("com.github.vlsi.stage-vote-release") version "1.77"
}

group = "io.github.furrrlo"
version = "1.1" + releaseParams.snapshotSuffix
description = "Small library which aims at resolving the command used to start the application"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withSourcesJar()
    withJavadocJar()
}

tasks.jar {
    manifest {
        attributes["Bundle-License"] = "MIT"
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Version"] = project.version
        attributes["Specification-Vendor"] = "JLaunchCmd"
        attributes["Specification-Version"] = project.version
        attributes["Specification-Title"] = "JLaunchCmd"
        attributes["Implementation-Vendor"] = "JLaunchCmd"
        attributes["Implementation-Vendor-Id"] = "io.github.furrrlo"
    }
    // Include the license
    CrLfSpec(LineEndings.LF).run {
        into("META-INF") {
            filteringCharset = "UTF-8"
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            textFrom("$projectDir/LICENSE")
        }
    }
}

if(releaseParams.release.get() && !JavaVersion.current().isJava9Compatible)
    throw Exception("Java 9 compatible compiler is needed for release builds")

if(JavaVersion.current().isJava9Compatible) {
    val moduleSourceSet = sourceSets.register("module")
    configurations.named("moduleCompileClasspath") { extendsFrom(configurations.compileClasspath.get()) }
    configurations.named("moduleRuntimeClasspath") { extendsFrom(configurations.runtimeClasspath.get()) }

    moduleSourceSet.configure { java.srcDirs(sourceSets.named("main").map { it.java.srcDirs }) }
    val compileModuleInfo = tasks.named<JavaCompile>(moduleSourceSet.map { it.compileJavaTaskName }.get()) {
        sourceCompatibility = JavaVersion.VERSION_1_9.toString()
        targetCompatibility = JavaVersion.VERSION_1_9.toString()

        modularity.inferModulePath.set(true)
    }

    val copyModuleInfo = tasks.register<Copy>("copyModuleInfo") {
        from(compileModuleInfo.map { it.destinationDirectory.file("module-info.class").get() })
        into(tasks.compileJava.map { it.destinationDirectory.dir("META-INF/versions/9/").get() })
    }

    tasks.compileJava { finalizedBy(copyModuleInfo) }
    tasks.classes { dependsOn(copyModuleInfo) }
    tasks.jar { manifest.attributes("Multi-Release" to "true") }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<AbstractArchiveTask>().configureEach {
    // Ensure builds are reproducible
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
    dirMode = "775".toInt(8)
    fileMode = "664".toInt(8)
}

repositories {
    mavenCentral()
}

dependencies {
//    "moduleCompileOnly"(sourceSets.main.map { it.output })

    val jna = "5.9.0"
    val jnaTestConfiguration = if(project.hasProperty("noJnaInTests")) "testCompileOnly" else "testImplementation"
    compileOnly("net.java.dev.jna:jna-jpms:$jna")
    jnaTestConfiguration("net.java.dev.jna:jna-jpms:$jna")
    compileOnly("net.java.dev.jna:jna-platform-jpms:$jna")
    jnaTestConfiguration("net.java.dev.jna:jna-platform-jpms:$jna")

    val junit = "5.8.1"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    systemProperty("junit.jna", !project.hasProperty("noJnaInTests"))
}

releaseParams {
    tlp.set("jlaunchcmd")
    organizationName.set("Furrrlo")
    componentName.set("jlaunchcmd")
    prefixForProperties.set("gh")
    svnDistEnabled.set(false)
    sitePreviewEnabled.set(false)
    nexus {
        prodUrl.set(project.uri("https://s01.oss.sonatype.org/service/local/"))
    }
    voteText.set {
        """
        ${it.componentName} v${it.version}-rc${it.rc} is ready for preview.
        Git SHA: ${it.gitSha}
        Staging repository: ${it.nexusRepositoryUri}
        """.trimIndent()
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            artifactId = project.name
            version = project.version.toString()
            description = project.description

            pom {
                name.set("JLaunchCmd")
                description.set(project.description)
                url.set("https://github.com/Furrrlo/JLaunchCmd")

                organization {
                    name.set("io.github.furrrlo")
                    url.set("https://github.com/Furrrlo")
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/Furrrlo/JLaunchCmd/issues")
                }

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/Furrrlo/JLaunchCmd/blob/master/LICENSE")
                        distribution.set("repo")
                    }
                }

                scm {
                    url.set("https://github.com/Furrrlo/JLaunchCmd")
                    connection.set("scm:git:git://github.com/furrrlo/jlaunchcmd.git")
                    developerConnection.set("scm:git:ssh://git@github.com:furrrlo/jlaunchcmd.git")
                }

                developers {
                    developer {
                        name.set("Francesco Ferlin")
                        url.set("https://github.com/Furrrlo")
                    }
                }
            }
        }
    }
}
