import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("org.jetbrains.dokka") version "0.10.1"
    kotlin("multiplatform") version "1.3.61"
    kotlin("plugin.serialization") version "1.3.61"
    `maven-publish`
}

repositories {
    jcenter()
    mavenCentral()
}

group = "io.piestack.multiplatform"
version = "0.0.1"

kotlin {
    jvm {
        val main by compilations.getting {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    js {
        browser {
        }
        nodejs {
        }
    }

    //linuxX64()
    //mingwX64()
    //macosX64()

    if (org.gradle.internal.os.OperatingSystem.current().isMacOsX) {
        //select iOS target platform depending on the Xcode environment variables
        val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
            if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
                ::iosArm64
            else
                ::iosX64

        iOSTarget("ios") {
            binaries {
                framework {
                    baseName = "MpesaAPI"
                }
            }
        }
    }

    val serializationVersion = "0.14.0"
    val ktorVersion = "1.3.1"
    val coroutinesVersion = "1.3.3"

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serializationVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                api("io.ktor:ktor-client-mock:$ktorVersion")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$coroutinesVersion")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion")

                implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
                implementation("io.ktor:ktor-client-json-jvm:$ktorVersion")
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization-jvm:$ktorVersion")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))

                implementation("junit:junit:4.12")

                api("io.ktor:ktor-client-mock-jvm:$ktorVersion")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:$serializationVersion")

                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("io.ktor:ktor-client-core-js:$ktorVersion")
                implementation("io.ktor:ktor-client-json-js:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization-js:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$coroutinesVersion")
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))

                api("io.ktor:ktor-client-mock-js:$ktorVersion")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$coroutinesVersion")
            }
        }

        if (org.gradle.internal.os.OperatingSystem.current().isMacOsX) {
            val iosMain by getting {
                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:$serializationVersion")

                    implementation("io.ktor:ktor-client-ios:$ktorVersion")
                    implementation("io.ktor:ktor-client-core-native:$ktorVersion")
                    implementation("io.ktor:ktor-client-json-native:$ktorVersion")
                    implementation("io.ktor:ktor-client-serialization-native:$ktorVersion")
                }
            }
            val iosTest by getting {
                dependencies {
                    api("io.ktor:ktor-client-mock-native:$ktorVersion")
                    api("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$coroutinesVersion")
                }
            }
        }
    }
}

if (org.gradle.internal.os.OperatingSystem.current().isMacOsX) {
//    task iosTest {
//        def device = project.findProperty("iosDevice")?.toString() ?: "iPhone 8"
//        dependsOn 'linkTestDebugExecutableIosx64'
//        group = JavaBasePlugin.VERIFICATION_GROUP
//        description = "Runs tests for target 'ios' on an iOS simulator"
//
//        doLast {
//            def binary = kotlin.targets.iosx64.compilations.test.getBinary('EXECUTABLE', 'DEBUG')
//            exec {
//                commandLine 'xcrun', 'simctl', 'spawn', device, binary.absolutePath
//            }
//        }
//    }
//    tasks.check.dependsOn(tasks.iosTest)
}

tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
    }
}

//val packForXcode by tasks.creating(Sync::class) {
//    val targetDir = File(buildDir, "xcode-frameworks")
//
//    /// selecting the right configuration for the iOS
//    /// framework depending on the environment
//    /// variables set by Xcode build
//    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
//    val framework = kotlin.targets
//        .getByName<KotlinNativeTarget>("ios")
//        .binaries.getFramework(mode)
//    inputs.property("mode", mode)
//    dependsOn(framework.linkTask)
//
//    from({ framework.outputDirectory })
//    into(targetDir)
//
//    /// generate a helpful ./gradlew wrapper with embedded Java path
//    doLast {
//        val gradlew = File(targetDir, "gradlew")
//        gradlew.writeText("#!/bin/bash\n"
//            + "export 'JAVA_HOME=${System.getProperty("java.home")}'\n"
//            + "cd '${rootProject.rootDir}'\n"
//            + "./gradlew \$@\n")
//        gradlew.setExecutable(true)
//    }

//val ktlint by configurations.creating
//
//dependencies {
//    ktlint("com.github.shyiko:ktlint:0.31.0")
//}
//
//val klintIdea by tasks.creating(JavaExec::class) {
//    description = "Apply ktlint rules to IntelliJ"
//    classpath = ktlint
//    main = "com.github.shyiko.ktlint.Main"
//    args = listOf("src/**/*.kt", "-F")
//}
