import com.github.vlsi.gradle.crlf.CrLfSpec
import com.github.vlsi.gradle.crlf.LineEndings
import com.github.vlsi.gradle.release.ReleaseExtension

plugins {
    java
    publishing
    id("com.github.vlsi.crlf")
    id("com.github.vlsi.gradle-extensions")
}
// TODO: can't apply it as it tries to hide the 'init' task and fails as it doesn't exist
apply(plugin = "com.github.vlsi.stage-vote-release")

description = "Small library which aims at resolving the command used to start the application"

java {
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

tasks.withType<AbstractArchiveTask>().configureEach {
    // Ensure builds are reproducible
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
    dirMode = "775".toInt(8)
    fileMode = "664".toInt(8)
}

val releaseParams = extensions.getByType<ReleaseExtension>()
releaseParams.apply {
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
        register<MavenPublication>("maven") {
            from(components["java"])

            val publication = this
            afterEvaluate {
                publication.artifactId = project.name
                publication.version = project.version.toString()
            }

            pom {
                name.set("JLaunchCmd")
                description.set(project.provider { project.description })
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
