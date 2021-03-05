![Optionals - Optional Parameters on the Java Platform](https://i.imgur.com/fz8ZopG.png)
<br>
![License](https://img.shields.io/github/license/maowimpl/optionals?style=flat-square)
![Latest Release](https://img.shields.io/github/v/release/maowimpl/optionals?include_prereleases&style=flat-square)
![Open Issues](https://img.shields.io/github/issues/maowimpl/optionals?style=flat-square)
![Open Pull Requests](https://img.shields.io/github/issues-pr/maowimpl/optionals?style=flat-square)

# Overview

**Optionals** is a library that adds powerful annotations that allow you to add optional parameters to your Java project.<br>
It was inspired by [Project Lombok](https://projectlombok.org/), and uses a similar annotation processor system to generate method overloads automatically, reducing boilerplate.<br>
Two major benefits of Optionals are:
* The compiled result has no dependency on Optionals, as the annotations and processor are only needed during compile-time.
* It's incredibly easy to use, by adding the `@Optional` annotation to a method's parameters, you're already done.

## Usage

As stated previously, Optionals is very plug-and-play. By adding it to your dependencies and adding the annotation to a method's parameters, you can get started in no time.<br>

**Example:**
* Source:
```java
public static void main(String[] args) {
  final MyClass clazz = new MyClass();
  clazz.myMethod();
  clazz.myMethod("My string.")
}

public void myMethod(@Optional String s) {
  System.out.println(s);
}
```
* Console:
```
null
My string.
```

## IDE Plugins

* [IntelliJ Platform](https://github.com/MaowImpl/optionals-intellij-plugin)

## Installation (Gradle)

1. Add JitPack to your buildscript's repositories.
```groovy
  repositories {
    maven { url 'https://jitpack.io' }
  }
```
2. Add the project to your buildscript's dependencies.
```groovy
  dependencies {
    compile 'com.github.maowimpl.optionals:optionals-annotations:<version>'
    annotationProcessor 'com.github.maowimpl.optionals:optionals:<version>'
  }
```
*View the version tracker above for the latest release.*

## Installation (Maven)
1. Add JitPack to your POM's repositories field.
```xml
  <repositories>
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
    </repository>
  </repositories>
```
2. Add the project to your POM's dependencies field.
```xml
  <dependency>
    <groupId>com.github.maowimpl.optionals</groupId>
    <artifactId>optionals-annotations</artifactId>
    <version>version</version>
  </dependency>

  <dependency>
    <groupId>com.github.maowimpl.optionals</groupId>
    <artifactId>optionals</artifactId>
    <version>version</version>
    <scope>provided</scope>
  </dependency>
```
*View the version tracker above for the latest release.*

## Notes

1. This project currently has limited IDE support, some features may not work and it may not be available for every platform.
2. I'm not sure about the compatibility of this and Lombok, as they're both similar systems that do a lot of modifications, proceed with your own caution.
