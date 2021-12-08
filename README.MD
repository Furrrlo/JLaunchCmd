# JLaunchCmd

[![Build](https://img.shields.io/github/workflow/status/Furrrlo/JLaunchCmd/Test)](https://github.com/Furrrlo/JLaunchCmd/actions/workflows/test.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.furrrlo/jlaunchcmd)](https://search.maven.org/artifact/io.github.furrrlo/jlaunchcmd)

Small library which aims at resolving the command used to start the application.

## Getting Started

### Maven

```xml
<dependency>
    <groupId>io.github.furrrlo</groupId>
    <artifactId>JLaunchCmd</artifactId>
    <version>1.0</version>
</dependency>
```

### Gradle

```kotlin
dependencies {
    implementation("io.github.furrrlo:JLaunchCmd:1.0")
}
```

## Usage

```java
import io.github.furrrlo.jlaunchcmd.JLaunchCmd;

import java.util.Arrays;

class Main {
    public static void main(String[] args) {
        final String[] launchCmd = JLaunchCmd.create().getLaunchCommand();
        System.out.println(Arrays.toString(launchCmd));
    }
}
```


