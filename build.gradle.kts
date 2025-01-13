plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
}

group = "eu.withoutaname"
version = "0.0.1"

application {
    mainClass.set("eu.withoutaname.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.logging.tools)
    implementation(libs.ktor.openapi.generator)
    implementation(libs.kotlinx.datetime)
    testImplementation(libs.bundles.testing.tools)
}
