import com.github.vlsi.gradle.release.ReleaseExtension

plugins {
    java
}

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

val releaseParams = extensions.findByType<ReleaseExtension>()
if(releaseParams != null && releaseParams.release.get() && !JavaVersion.current().isJava9Compatible)
    throw Exception("Java 9 compatible compiler is needed for release builds")

dependencies {
//    "moduleCompileOnly"(sourceSets.main.map { it.output })
}
