import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.32"
    application
}

group = "me.augustoalonso"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        setUrl("https://oss.sonatype.org/content/repositories/snapshots")
    }
//    maven {
//        url(/**/"https://oss.sonatype.org/content/repositories/snapshots")
//    }
}

dependencies {
    testImplementation(kotlin("test-junit"))

    implementation("org.igniterealtime.smack:smack-java8:4.4.3")
    // Optional for XMPPTCPConnection
    implementation("org.igniterealtime.smack:smack-tcp:4.4.3")
    // Optional for XMPP-IM (RFC 6121) support (Roster, Threaded Chats, …)
    implementation("org.igniterealtime.smack:smack-im:4.4.3")
    // Optional for XMPP extensions support
    implementation("org.igniterealtime.smack:smack-extensions:4.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")

}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}