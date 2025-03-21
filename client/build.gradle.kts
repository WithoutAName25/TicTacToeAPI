plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(project(":common"))
    implementation(libs.bundles.client)
}

java {
    withSourcesJar()
    withJavadocJar()
}
