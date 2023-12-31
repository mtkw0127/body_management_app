buildscript {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.10")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.8.2.0")
        classpath("com.google.gms:google-services:4.4.0")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
        classpath("com.ncorti.ktfmt.gradle:plugin:0.10.0")
        classpath("com.github.triplet.gradle:play-publisher:4.0.0-SNAPSHOT")
    }
}

plugins {
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.3" apply false
}

