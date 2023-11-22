# GradleAccessWidener
Gradle support for Fabric's access wideners


### Example usage:
```kotlin
plugins {
    id("io.github.redstonneur1256.gradle-access-widener") version "0.1"
}

configure<AccessWidenerExtension> {
    paths = files("path/to/file.accessWidener")
}
```