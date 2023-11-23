# GradleAccessWidener
Gradle support for [Fabric's access wideners](https://fabricmc.net/wiki/tutorial:accesswideners)


### Usage:

- Apply plugin
  ```kotlin
  plugins {
      id("io.github.redstonneur1256.gradle-access-widener") version "0.1"
  }
  ```
- Configure the path to your access widener file(s)
  ```kotlin
  configure<AccessWidenerExtension> {
      paths = files("path/to/file.accessWidener")
  }
  ```
- If using IntelliJ: reload the gradle project for the access wideners to apply.
  A reload is required every time you update any access widener configuration.
  The access wideners are applied automatically when building the project.

Documentation for access widener structure accessible [here](https://fabricmc.net/wiki/tutorial:accesswideners).