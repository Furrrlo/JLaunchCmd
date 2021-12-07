# JLaunchCmd

Small library which aims at resolving the command used to start the application.

## Getting Started

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Then add the dependency
```xml
<dependency>
    <groupId>com.github.furrrlo</groupId>
    <artifactId>JLaunchCmd</artifactId>
    <version>1.0</version>
</dependency>
```

### Gradle

```kotlin
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation("com.github.furrrlo:JLaunchCmd:1.0")
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


