import net.researchgate.release.GitAdapter.GitConfig
import java.util.Date
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

/*
 * Copyright 2022 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

plugins {
  id("java")
  id("maven")
  id("java-gradle-plugin")
  id("net.researchgate.release") version "2.6.0"
  id("com.github.sherter.google-java-format") version "0.9"
  id("checkstyle")
  id("jacoco")
}

repositories {
  mavenCentral()
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

group = "com.google.cloud.tools"

dependencies {
  implementation(localGroovy())
  implementation(gradleApi())
  api("com.google.cloud.tools:appengine-plugins-core:0.10.0")

  testImplementation("commons-io:commons-io:2.11.0")
  testImplementation("junit:junit:4.13.2")
  testImplementation("org.hamcrest:hamcrest-library:2.2")
  testImplementation("org.mockito:mockito-core:4.11.0")
}


tasks.wrapper {
  gradleVersion = "6.9"
}

tasks.jar.configure {
  manifest {
    attributes(
      mapOf(
        "Implementation-Title" to project.name,
        "Implementation-Version" to project.version,
        "Built-By" to System.getProperty("user.name"),
        "Built-Date" to Date(),
        "Built-JDK" to System.getProperty("java.version"),
        "Built-Gradle" to gradle.gradleVersion
      )
    )
  }
}

tasks.withType<JavaCompile>().configureEach { 
    options.compilerArgs = options.compilerArgs + listOf(
      "-Xlint:all"
    )
}

// Gradle 6 needs a special treatment for Guava 31+; otherwise you get "... However we
// cannot choose between the following variants..." error.
// https://github.com/google/guava/releases/tag/v32.1.0
sourceSets.all {
  configurations.getByName(runtimeClasspathConfigurationName) {
    attributes.attribute(Attribute.of("org.gradle.jvm.environment", String::class.java), "standard-jvm")
  }
  configurations.getByName(compileClasspathConfigurationName) {
    attributes.attribute(Attribute.of("org.gradle.jvm.environment", String::class.java), "standard-jvm")
  }
}

/* TESTING */
tasks.test.configure {
  testLogging {
    showStandardStreams = true
    exceptionFormat = FULL
  }
}

sourceSets {
  create("integTest") {
    compileClasspath += main.get().output
    runtimeClasspath += main.get().output
  }
}

configurations {
  named("integTestCompile").get().extendsFrom(testCompileClasspath.get())
  named("integTestRuntime").get().extendsFrom(testRuntimeClasspath.get())
}

tasks.register<Test>("integTest") {
  testClassesDirs = sourceSets.getByName("integTest").output.classesDirs
  classpath = sourceSets.getByName("integTest").runtimeClasspath
  outputs.upToDateWhen { false }
}
/* TESTING */


/* RELEASING */
tasks.register<Jar>("sourceJar") {
  from(sourceSets.main.get().allJava)
  archiveClassifier.set("sources")
}

tasks.register<Jar>("javadocJar") {
  dependsOn(tasks.javadoc)
  from(tasks.javadoc.map { it.destinationDir!! })
  archiveClassifier.set("javadoc")
}

project.afterEvaluate {
  tasks.register("writePom") {
    val outputFile = file("$buildDir/pom/${project.name}-${project.version}.pom")
    outputs.file(outputFile)

    doLast {
      maven {
        pom {
          project {
            withGroovyBuilder {
              "name"("App Engine Gradle Plugin")
              "description"("This Gradle plugin provides tasks to build and deploy Google App Engine applications.")

              "url"("https://github.com/GoogleCloudPlatform/app-gradle-plugin")
              "inceptionYear"("2016")

              "scm" {
                "url"("https://github.com/GoogleCloudPlatform/app-gradle-plugin")
                "connection"("scm:https://github.com/GoogleCloudPlatform/app-gradle-plugin.git")
                "developerConnection"("scm:git://github.com/GoogleCloudPlatform/app-gradle-plugin.git")
              }

              "licenses" {
                "license" {
                  "name"("The Apache Software License, Version 2.0")
                  "url"("http://www.apache.org/licenses/LICENSE-2.0.txt")
                  "distribution"("repo")
                }
              }
              "developers" {
                "developer" {
                  "id"("loosebazooka")
                  "name"("Appu Goundan")
                  "email"("appu@google.com")
                }
              }
            }
          }
        }.writeTo(outputFile)
      }
    }
  }
}


// for kokoro releases
tasks.register<Sync>("prepareRelease") {
  from(tasks.jar)
  from(tasks.named("sourceJar"))
  from(tasks.named("javadocJar"))
  from(tasks.named("writePom"))

  into("${buildDir}/release-artifacts")

  dependsOn(tasks.build)
}

release {
  tagTemplate = "v\$version"
  getProperty("git").apply {
    this as GitConfig
    requireBranch = """^release-v\d+.*$"""  //regex
  }
}
/* RELEASING */

/* FORMATTING */
googleJavaFormat {
  toolVersion = "1.7"
}


tasks.check.configure {
  dependsOn(tasks.verifyGoogleJavaFormat)
}
tasks.withType<Checkstyle>().configureEach {
  // Set up a soft dependency so that verifyGoogleFormat suggests running googleJavaFormat,
  // before devs start fixing individual checkstyle violations manually.
  shouldRunAfter(tasks.verifyGoogleJavaFormat)
}
// to auto-format run ./gradlew googleJavaFormat

checkstyle {
  toolVersion = "8.37"
  // Get the google_checks.xml file from the actual tool we're invoking.
  config = resources.text.fromArchiveEntry(configurations.checkstyle.get().files.first(), "google_checks.xml")
  maxErrors = 0
  maxWarnings = 0
  tasks.checkstyleTest.configure {
    enabled = false
  }
}
/* FORMATTING */

/* TEST COVERAGE */
jacoco {
  toolVersion = "0.8.8"
}

tasks.jacocoTestReport {
  reports {
    xml.isEnabled = true
    html.isEnabled = false
  }
}
/* TEST COVERAGE */
