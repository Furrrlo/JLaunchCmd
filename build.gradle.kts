plugins {
    `java-library`
}

group = "com.github.furrrlo"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

if(JavaVersion.current().isJava9Compatible) {
    val moduleSourceSet = sourceSets.register("module")
    configurations.named("moduleCompileClasspath") { extendsFrom(configurations.compileClasspath.get()) }
    configurations.named("moduleRuntimeClasspath") { extendsFrom(configurations.runtimeClasspath.get()) }
    val compileModuleInfo = tasks.register<JavaCompile>("compileModuleInfo") {
        sourceCompatibility = JavaVersion.VERSION_1_9.toString()
        targetCompatibility = JavaVersion.VERSION_1_9.toString()

        source(moduleSourceSet.map { it.java })
        source(tasks.compileJava.map { it.source })

        classpath = project.files(moduleSourceSet.map { it.compileClasspath })
        destinationDirectory.set(moduleSourceSet.map { it.java.destinationDirectory }.map { it.get() })
        modularity.inferModulePath.set(true)
    }

    val copyModuleInfo = tasks.register<Copy>("copyModuleInfo") {
        from(compileModuleInfo.map { it.destinationDirectory.file("module-info.class").get() })
        into(tasks.compileJava.map { it.destinationDirectory.get() })
    }

    tasks.compileJava { finalizedBy(copyModuleInfo) }
    tasks.classes { dependsOn(copyModuleInfo) }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
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