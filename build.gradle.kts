val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val koinVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.7.10"
                id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10"
}

group = "com.example"
version = "0.0.1"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

tasks.create("stage") {
    dependsOn("installDist")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")

    // StatusPages
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")

    // DefaultHeaders
    implementation("io.ktor:ktor-server-default-headers:$ktorVersion")

    // Json
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // ContentNegotiation
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")

    // CallLogging
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")

    // Koin for Ktor
    implementation ("io.insert-koin:koin-ktor:$koinVersion")
    // SLF4J Logger
    implementation ("io.insert-koin:koin-logger-slf4j:$koinVersion")
    // Koin testing tools
    implementation ("io.insert-koin:koin-test:$koinVersion")
    // Needed JUnit version
    implementation ("io.insert-koin:koin-test-junit4:$koinVersion")

}