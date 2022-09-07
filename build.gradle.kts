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
import net.researchgate.release.GitAdapter.GitConfig
import java.util.Date
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

plugins {
    id("java")
    id("maven-publish")
    id("java-gradle-plugin")
    id("net.researchgate.release") version "2.6.0"
    id("com.github.sherter.google-java-format") version "0.8"
    id("checkstyle")
    id("jacoco")
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

group = "com.google.cloud.tools"

dependencies {
    implementation(localGroovy())
    implementation(gradleApi())
    implementation("com.google.cloud.tools:appengine-plugins-core:0.9.9")

    testImplementation("commons-io:commons-io:2.4")
    testImplementation("junit:junit:4.12")
    testImplementation("org.hamcrest:hamcrest-library:1.3")
    testImplementation("org.mockito:mockito-core:2.23.4")
}


tasks.wrapper {
    gradleVersion = "6.9"
}

tasks.jar {
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

// <editor-fold defaultstate="expanded" desc="Testing">
gradlePlugin {
    plugins {
        create("appengine") {
            id = "$group.appengine"
            implementationClass = "com.google.cloud.tools.gradle.appengine.AppEnginePlugin"
            displayName = "App Engine Gradle Plugin"
            description =
                "This Gradle plugin provides tasks to build and deploy Google App Engine applications."
        }
        create("appengine-appenginewebxml") {
            id = "$group.appengine-appenginewebxml"
            implementationClass =
                "com.google.cloud.tools.gradle.appengine.standard.AppEngineStandardPlugin"
            displayName = "App Engine Gradle Plugin"
            description =
                "This Gradle plugin provides tasks to build and deploy Google App Engine applications."
        }
        create("appengine-appyaml") {
            id = "$group.appengine-appyaml"
            implementationClass =
                "com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlPlugin"
            displayName = "App Engine Gradle Plugin"
            description =
                "This Gradle plugin provides tasks to build and deploy Google App Engine applications."
        }
        create("appengine-flexible") {
            id = "$group.appengine-flexible"
            implementationClass =
                "com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlPlugin"
            displayName = "App Engine Gradle Plugin"
            description =
                "This Gradle plugin provides tasks to build and deploy Google App Engine applications."
        }
        create("appengine-standard") {
            id = "$group.appengine-standard"
            implementationClass =
                "com.google.cloud.tools.gradle.appengine.standard.AppEngineStandardPlugin"
            displayName = "App Engine Gradle Plugin"
            description =
                "This Gradle plugin provides tasks to build and deploy Google App Engine applications."
        }
        create("source-context") {
            id = "$group.source-context"
            implementationClass =
                "com.google.cloud.tools.gradle.appengine.sourcecontext.SourceContextPlugin"
            displayName = "App Engine Gradle Plugin"
            description =
                "This Gradle plugin provides tasks to build and deploy Google App Engine applications."
        }
    }
}

// <editor-fold defaultstate="expanded" desc="Testing">
tasks.test {
    testLogging {
        showStandardStreams = true
        exceptionFormat = FULL
    }
}

val integTestSourceSet = sourceSets.create("integTest") {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}

configurations {
    "integTestImplementation" {
        extendsFrom(testImplementation.get())
    }
    "integTestRuntime" {
        extendsFrom(testRuntime.get())
    }
}

val integTest by tasks.registering(Test::class) {
    testClassesDirs = integTestSourceSet.output.classesDirs
    classpath = integTestSourceSet.runtimeClasspath
    outputs.upToDateWhen { false }
}
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Releasing">
val generatePom by tasks.registering {
    description = "Generates all the pom files to ${buildDir.name}/publications/ for manual inspection"
    group = "release"
    dependsOn(tasks.withType<GenerateMavenPom>())
}

val tempRepoPath = "$buildDir/repo"

val cleanTempRepo by tasks.registering(Delete::class) {
    delete(tempRepoPath)
}

tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn(cleanTempRepo)
}

publishing {
    // `./gradlew publish` would generate the release artifacts to $buildDir/repo folder
    repositories {
        maven {
            url = uri(tempRepoPath)
        }
    }
    publications {
        withType<MavenPublication> {
            // Use the resolved versions in pom.xml
            // Gradle might have different resolution rules, so we set the versions
            // that were used in Gradle build/test.
            versionMapping {
                usage(Usage.JAVA_RUNTIME) {
                    fromResolutionResult()
                }
                usage(Usage.JAVA_API) {
                    fromResolutionOf("runtimeClasspath")
                }
            }
            // Skip name and description for plugin markers
            if (name == "pluginMaven") {
                pom {
                    name.set("App Engine Gradle Plugin")
                    description.set("This Gradle plugin provides tasks to build and deploy Google App Engine applications.")
                }
            }
            pom {
                // <editor-fold defaultstate="collapsed" desc="Default POM values">
                developers {
                    developer {
                        id.set("loosebazooka")
                        name.set("Appu Goundan")
                        email.set("appu@google.com")
                    }
                }
                url.set("https://github.com/GoogleCloudPlatform/app-gradle-plugin")
                inceptionYear.set("2016")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        comments.set("A business-friendly OSS license")
                        distribution.set("repo")
                    }
                }
                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues")
                }
                scm {
                    url.set("https://github.com/GoogleCloudPlatform/app-gradle-plugin")
                    connection.set("scm:https://github.com/GoogleCloudPlatform/app-gradle-plugin.git")
                    developerConnection.set("scm:git://github.com/GoogleCloudPlatform/app-gradle-plugin.git")
                }
                // </editor-fold>
            }
        }
    }
}


// for kokoro releases
val prepareRelease by tasks.registering(Sync::class) {
    description = "Copy release artifacts to ${buildDir.name}/release-artifacts/"
    group = "release"
    dependsOn(tasks.publish)
    into("$buildDir/release-artifacts")
    from(tempRepoPath) {
        include("**/appengine-gradle-plugin/**/*.jar")
        include("**/appengine-gradle-plugin/**/*.pom")
        // Flatten hierarchy
        eachFile {
            path = "plugin-artifacts/$name"
        }
    }
    from(tempRepoPath) {
        include("**/*.gradle.plugin/**/*.pom")
        // Flatten hierarchy
        eachFile {
            path = "plugin-markers/$name"
        }
    }
    // Flattening the hierarchy leaves empty directories,
    // do not copy those
    includeEmptyDirs = false
}

release {
    tagTemplate = "v\$version"
    getProperty("git").apply {
        this as GitConfig
        requireBranch = """^release-v\d+.*$"""  //regex
    }
}
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Formatting">
googleJavaFormat {
    toolVersion = "1.7"
}


tasks.check {
    dependsOn(tasks.verifyGoogleJavaFormat)
}
// to auto-format run ./gradlew googleJavaFormat

checkstyle {
    toolVersion = "8.18"
    // get the google_checks.xml file from the actual tool we"re invoking)
    config = resources.text.fromArchiveEntry(
        configurations.checkstyle.map { it.first() },
        "google_checks.xml"
    )
    maxErrors = 0
    maxWarnings = 0
}

tasks.checkstyleTest {
    enabled = false
}
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Test coverage">
jacoco {
    toolVersion = "0.8.6"
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        html.isEnabled = false
    }
}
// </editor-fold>
